package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithoutOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;

    private User user = new User(null, "Linar", "Linar@xakep.ru");

    private User requestor = new User(null, "Lenar", "Lenar@xakep.ru");

    private User owner = new User(null, "Nastya", "Nastya@xakep.ru");

    private final ItemRequestDto requestDto = new ItemRequestDto(null,
            "Нужна алмазная пила", user, LocalDateTime.now());

    private ItemRequest request = new ItemRequest(null,
            "Нужна алмазная пила", user, LocalDateTime.now());

    private final ItemRequestDto requestDtoNumberTwo = new ItemRequestDto(null,
            "Нужна алмазная пила", user, LocalDateTime.now());

    private List<ItemDtoWithoutOwner> items = new ArrayList<>();

    private final ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(
            2L,
            "Нужна газонокосилка",
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            items);

    private final Item itemWithRequest = new Item(null, "Газонокосила", "Газонокосилка",
            true, owner, request);

    private final Item itemWithRequestNumberOne = new Item(null, "Газонокосила", "Газонокосилка BOSCH",
            true, owner, request);

    private final Item itemWithRequestNumberTree = new Item(null, "Газонокосила", "Газонокосилка Юпитер",
            true, owner, request);

    private Item itemNumberOne = new Item(1L, "Пила", "Пила",
            true, owner, null);

    @Test
    public void addNewItemRequest() {
        user = userService.createUser(user);
        requestor = userService.createUser(requestor);
        request = itemRequestService.addNewItemRequest(requestor.getId(), requestDto);

        Optional<ItemRequest> requestSaved = itemRequestService.findItemRequestById(request.getId());

        assertTrue(requestSaved.isPresent());
    }

    @Test
    public void getAllItemRequests() {
        owner = userService.createUser(owner);
        user = userService.createUser(user);
        requestor = userService.createUser(requestor);
        request = itemRequestService.addNewItemRequest(requestor.getId(), requestDto);
        ItemDto itemDto = itemService.addNewItemWithRequest(owner.getId(), itemNumberOne, request.getId());

        List<ItemRequestDtoWithAnswers> requests = new ArrayList<>();
        requests.add(itemRequestDtoWithAnswers);

        List<ItemRequestDtoWithAnswers> requestsActual = itemRequestService.getAllItemRequests(user.getId(),
                0, 20);

        assertEquals(requests.size(), requestsActual.size());
    }

    public void getAllItemRequestsByRequestorId() {
        owner = userService.createUser(owner);
        user = userService.createUser(user);
        requestor = userService.createUser(requestor);
        request = itemRequestService.addNewItemRequest(requestor.getId(), requestDto);
        ItemDto itemDto = itemService.addNewItemWithRequest(owner.getId(), itemNumberOne, request.getId());

        List<ItemRequestDtoWithAnswers> requests = new ArrayList<>();
        requests.add(itemRequestDtoWithAnswers);

        List<ItemRequestDtoWithAnswers> requestsActual = itemRequestService.getAllItemRequests(requestor.getId(),
                0, 20);

        assertEquals(requests.size(), requestsActual.size());
    }

    @Test
    public void getItemRequestById() {
        owner = userService.createUser(owner);
        user = userService.createUser(user);
        requestor = userService.createUser(requestor);
        request = itemRequestService.addNewItemRequest(requestor.getId(), requestDtoNumberTwo);
        ItemDto itemDto = itemService.addNewItemWithRequest(owner.getId(), itemNumberOne, request.getId());

        ItemRequestDtoWithAnswers requestSaved = itemRequestService.getItemRequestById(user.getId(), request.getId());

        assertEquals(requestSaved.getDescription(), requestDtoNumberTwo.getDescription());
    }

    @Test
    @Transactional
    public void findItemRequestsByUserId() {
        owner = userService.createUser(owner);
        user = userService.createUser(user);
        requestor = userService.createUser(requestor);
        request = itemRequestService.addNewItemRequest(requestor.getId(), requestDto);

        ItemDto itemDto = itemService.addNewItemWithRequest(owner.getId(), itemNumberOne, request.getId());
        List<ItemRequestDtoWithAnswers> bookingSaved = itemRequestService.findItemRequestsByUserId(requestor.getId());
        System.out.println(bookingSaved);
        List<ItemRequestDtoWithAnswers> requests = new ArrayList<>();
        requests.add(itemRequestDtoWithAnswers);

        assertEquals(bookingSaved.size(), requests.size());
    }

    @Test
    public void findItemRequestById() {
        owner = userService.createUser(owner);
        requestor = userService.createUser(requestor);
        request = itemRequestService.addNewItemRequest(requestor.getId(), requestDtoNumberTwo);
        ItemDto itemDto = itemService.addNewItemWithRequest(owner.getId(), itemWithRequest, request.getId());

        Optional<ItemRequest> bookingSaved = itemRequestService.findItemRequestById(request.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void findItemRequestWithId() {
        ItemRequestNotFoundException thrown = assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.findItemRequestById(777),
                "Запрос с id  = [777] не существует"
        );

        assertTrue(thrown.getMessage().contains("Запрос с id  = [777] не существует"));
    }
}