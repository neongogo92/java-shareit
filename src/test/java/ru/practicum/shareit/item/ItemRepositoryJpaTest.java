package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    private Item item;
    private User owner;
    PageRequest page = PageRequest.of(0, 1);


    @BeforeEach
    void saveData() {
        owner = new User();
        owner.setName("Гарри Поттер");
        owner.setEmail("aliveboy@yandex.ru");
        em.persist(owner);

        item = new Item();
        item.setName("Палочка бузинная");
        item.setDescription("Длина 15 дюймов");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);
        em.flush();
    }

    @Test
    void testFindItemsByTextByName() {
        List<Item> resultItems = itemRepository.findItemsByText("палочка", page);

        assertEquals(resultItems.size(), 1);
        Item resultItem = resultItems.get(0);
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertEquals(resultItem.getAvailable(), item.getAvailable());
        assertEquals(resultItem.getOwner(), item.getOwner());
    }

    @Test
    void testFindItemsByTextByDescription() {
        List<Item> resultItems = itemRepository.findItemsByText("длин", page);

        assertEquals(resultItems.size(), 1);
        Item resultItem = resultItems.get(0);
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertEquals(resultItem.getAvailable(), item.getAvailable());
        assertEquals(resultItem.getOwner(), item.getOwner());
    }

    @Test
    void testFindItemsByTextWithNoFound() {
        List<Item> resultItems = itemRepository.findItemsByText("метла", page);

        assertEquals(resultItems.size(), 0);
    }

    @Test
    void testFindByOwner_Id() {
        List<Item> resultItems = itemRepository.findByOwner_Id(owner.getId());

        assertEquals(1, resultItems.size());
    }

    @Test
    void testFindByIdAndOwner_IdIsNot() {
        Optional<Item> result = itemRepository.findByIdAndOwner_IdIsNot(item.getId(), owner.getId() + 1);

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByIdAndOwner_Id() {
        Optional<Item> result = itemRepository.findByIdAndOwner_Id(item.getId(), owner.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByItemRequest_Id() {
        User requestor = new User();
        requestor.setName("Волдеморт");
        requestor.setEmail("noname@mail.ru");
        em.persist(requestor);

        ItemRequest request = new ItemRequest();
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);
        request.setDescription("Срочно нужна палочка.");
        em.persist(request);

        Item item = new Item();
        item.setName("Палочка");
        item.setDescription("Сделана из бузины");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setItemRequest(request);
        em.persist(item);
        em.flush();

        List<Item> resultItems = itemRepository.findByItemRequest_Id(request.getId());
        assertEquals(1, resultItems.size());
    }

    @AfterEach
    void deleteData() {
        itemRepository.deleteAll();
    }
}
