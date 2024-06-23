package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.ItemDtoWithoutOwner;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

class ItemRequestServiceImplTest {

    ItemRequestService itemRequestService = Mockito.mock(ItemRequestService.class);

    private final User user = new User(1L, "Linar", "Linar@xakep.ru");

    private final User userNumberTwo = new User(null, "Lenar", "Lenar@xakep.ru");

    private final ItemRequestDto requestDto = new ItemRequestDto(1L,
            "Нужна алмазная пила", user, LocalDateTime.now());

    private final ItemRequest request = new ItemRequest(1L,
            "Нужна алмазная пила", user, LocalDateTime.now());

    List<ItemDtoWithoutOwner> items = new ArrayList<>();

    private final ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(
            2L,
            "Нужна газонокосилка",
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            items);

    @Test
    void addNewItemRequest() {
        Mockito
                .when(itemRequestService.addNewItemRequest(anyLong(), any()))
                .thenReturn(request);

        itemRequestService.addNewItemRequest(1L, requestDto);
        Mockito.verify(itemRequestService, Mockito.times(1))
                .addNewItemRequest(1L, requestDto);
    }

    @Test
    void getAllItemRequests() {
        List<ItemRequestDtoWithAnswers> requests = new ArrayList<>();
        requests.add(itemRequestDtoWithAnswers);

        Mockito
                .when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(requests);

        itemRequestService.getAllItemRequests(1L, 0, 15);
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllItemRequests(1L, 0, 15);
    }

    @Test
    void getItemRequestById() {
        Mockito
                .when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoWithAnswers);

        itemRequestService.getItemRequestById(1L, 2L);
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getItemRequestById(1L, 2L);
    }

}