package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void search() {
        User user = new User(1L, "Linar", "Linar@xakep.ru");

        user = userRepository.save(user);

        Item item = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
                true, user, null);

        item = repository.save(item);

        List<Item> expectedItems = new ArrayList<>();
        expectedItems.add(item);

        List<Item> actualItems = repository.search("МуЛьТи");

        assertEquals(expectedItems, actualItems);
    }
}