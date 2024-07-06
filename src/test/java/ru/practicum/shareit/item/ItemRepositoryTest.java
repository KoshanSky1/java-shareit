package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Test
    public void findAllByOwnerId() {
        User user = new User(12L, "Max", "Max@mail.ru");

        Item item = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
                true, user, null);

        List<Item> expectedItems = new ArrayList<>();

        List<Item> actualItems = repository.findAllByOwnerId(1L);

        assertEquals(expectedItems, actualItems);

    }

    @Test
    public void findAllByRequestIn() {
        User user = new User(12L, "Max", "Max@mail.ru");

        Item item = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
                true, user, null);

        List<Item> expectedItems = new ArrayList<>();

        List<ItemRequest> itemRequests = new ArrayList<>();

        List<Item> actualItems = repository.findAllByRequestIn(itemRequests);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void findAllByRequestId() {
        User user = new User(12L, "Max", "Max@mail.ru");

        Item item = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
                true, user, null);

        List<Item> expectedItems = new ArrayList<>();

        List<Item> actualItems = repository.findAllByRequestId(8L);

        assertEquals(expectedItems, actualItems);
    }
}