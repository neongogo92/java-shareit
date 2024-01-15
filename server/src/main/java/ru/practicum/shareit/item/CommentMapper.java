package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) return null;
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static Comment toComment(CommentDto dto, User author, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(dto.getText());

        // Создаем LocalDateTime из отдельных компонентов
        LocalDateTime created = LocalDateTime.of(
                dto.getYear(), dto.getMonth(), dto.getDay(),
                dto.getHour(), dto.getMinute(), dto.getSecond()
        );
        comment.setCreated(created);

        return comment;
    }
}
