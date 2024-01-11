package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public ItemShortDto addItem(long userId, ItemShortDto itemDto) {
        findUserIfExists(userId);
        UserDto user = userService.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto, UserMapper.toUser(user));
        item.setOwner(UserMapper.toUser(user));
        item = itemRepository.save(item);
        return ItemMapper.toItemShortDto(item);
    }

    @Override
    public ItemShortDto updateItem(long userId, long itemId, ItemShortDto itemDto) {
        findUserIfExists(userId);
        Item item = itemRepository.findByIdAndOwner_Id(itemId, userId);
        if (item == null) {
            throw new NotFoundException("Данная вещь для указанного пользователя не найдена.");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemShortDto(item);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        findUserIfExists(userId);

        Item item = findItemIfExists(itemId);
        List<Comment> comments = commentRepository.findByItem_Id(item.getId());
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookingsForItem = bookingRepository.findByItem_Id(item.getId());
            return ItemMapper.toItemDto(item, CommentMapper.toCommentDtoList(comments), bookingsForItem);
        }
        return ItemMapper.toItemDto(item, CommentMapper.toCommentDtoList(comments), null);
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        findUserIfExists(userId);
        List<Item> foundItems = itemRepository.findByOwner_Id(userId);
        List<Long> itemIds = foundItems.stream()
                .map(Item::getId)
                .collect(toList());

        List<Comment> comments = commentRepository.findByItem_IdIn(itemIds);
        Map<Item, List<Comment>> commentsMap = comments
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        List<Booking> bookings = bookingRepository.findByItem_IdIn(itemIds);
        Map<Item, List<Booking>> bookingsMap = bookings
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<ItemDto> foundItemsDto = new ArrayList<>();
        for (Item item : foundItems) {
            List<Comment> itemComments = commentsMap.get(item);
            List<Booking> itemBookings = bookingsMap.get(item);
            foundItemsDto.add(ItemMapper.toItemDto(item, CommentMapper.toCommentDtoList(itemComments), itemBookings));
        }
        return foundItemsDto;
    }

    @Override
    public List<ItemShortDto> searchItems(long userId, String text) {
        findUserIfExists(userId);
        if (text.isBlank()) return new ArrayList<>();
        List<Item> foundItems = itemRepository.findItemsByText(text);
        return ItemMapper.toItemShortDtoList(foundItems);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        UserDto user = userService.findUserById(userId);
        Item item = findItemIfExists(itemId);
        List<Booking> itemBookings = bookingRepository.findByItem_IdAndBooker_IdAndEndBefore(itemId, userId,
                LocalDateTime.now());
        if (itemBookings.isEmpty()) {
            throw new ValidationException("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, UserMapper.toUser(user), item));
        return CommentMapper.toCommentDto(comment);
    }

    private void findUserIfExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
       }

    private Item findItemIfExists(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найденa."));
    }
}