package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoWithBooking;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplDataBaseTest {
    private final UserServiceImpl userService;

    private final ItemServiceImpl itemService;

    private final User userNumberOne = new User(null, "Linar", "Linar@xakep.ru");

    private final User userNumberTwo = new User(null, "Lenar", "Lenar@xakep.ru");

    private final Item itemNumberOne = new Item(null, "Алмазная пила", "Алмазная пила Makita",
            true, userNumberTwo, null);

    private final Item itemNumberTwo = new Item(null, "Мультипекарь", "Мультипекарь Redmond",
            true, userNumberOne, null);

    private final Item itemNumberThree = new Item(null, "Отвертка", "Отвертка крестовая",
            true, userNumberOne, null);

    @Test
    void getItems() {
        User userNumberOneSaved = userService.createUser(userNumberOne);
        User userNumberTwoSaved = userService.createUser(userNumberTwo);

        ItemDto itemNumberOneSaved = itemService.addNewItemWithoutRequest(userNumberTwo.getId(), itemNumberOne);
        ItemDto itemNumberTwoSaved = itemService.addNewItemWithoutRequest(userNumberOne.getId(), itemNumberTwo);
        ItemDto itemNumberThreeSaved = itemService.addNewItemWithoutRequest(userNumberOne.getId(), itemNumberThree);

        List<ItemDtoWithBooking> itemsExpected = new ArrayList<>();

        itemsExpected.add(toItemDtoWithBooking(toItem(itemNumberTwoSaved), null, null,
                null));
        itemsExpected.add(toItemDtoWithBooking(toItem(itemNumberThreeSaved), null, null,
                null));

        List<ItemDtoWithBooking> itemsActual = itemService.getItems(userNumberOneSaved.getId(), 0, 20);

        assertEquals(itemsExpected, itemsActual);

    }
}