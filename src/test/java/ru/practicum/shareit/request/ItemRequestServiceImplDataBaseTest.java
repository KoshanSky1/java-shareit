package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplDataBaseTest {

    private final UserServiceImpl userService;

    private final ItemServiceImpl itemService;

    private final ItemRequestServiceImpl requestService;

    private final User userNumberOne = new User(null, "Linar", "Linar@xakep.ru");

    private final User userNumberTwo = new User(null, "Lenar", "Lenar@xakep.ru");

    private final ItemRequestDto requestNumberOne = new ItemRequestDto(null,
            "Нужна алмазная пила", userNumberOne, LocalDateTime.now());

    private final ItemRequestDto requestNumberTwo = new ItemRequestDto(null,
            "Нужен мультипекарь", userNumberTwo, LocalDateTime.now());

    private final Item itemNumberOne = new Item(null, "Алмазная пила", "Алмазная пила Makita",
            true, userNumberTwo, null);

    private final Item itemNumberTwo = new Item(null, "Мультипекарь", "Мультипекарь Redmond",
            true, userNumberOne, null);


    @Test
    void getAllItemRequests() {
        User userNumberOneSaved = userService.createUser(userNumberOne);
        User userNumberTwoSaved = userService.createUser(userNumberTwo);

        ItemRequest requestNumberOneSaved = requestService.addNewItemRequest(userNumberOne.getId(), requestNumberOne);
        ItemRequest requestNumberTwoSaved = requestService.addNewItemRequest(userNumberTwo.getId(), requestNumberTwo);

        ItemDto itemNumberOneSaved = itemService.addNewItemWithRequest(userNumberTwo.getId(), itemNumberOne,
                1L);
        ItemDto itemNumberTwoSaved = itemService.addNewItemWithRequest(userNumberOne.getId(), itemNumberTwo,
                2L);

        List<ItemRequestDtoWithAnswers> requestsExpected = new ArrayList<>();

        requestsExpected.add(requestService.getItemRequestById(userNumberOneSaved.getId(), itemNumberOneSaved.getId()));

        List<ItemRequestDtoWithAnswers> requests = requestService.findItemRequestsByUserId(userNumberOneSaved.getId());

        assertEquals(requestsExpected, requests);

    }
}