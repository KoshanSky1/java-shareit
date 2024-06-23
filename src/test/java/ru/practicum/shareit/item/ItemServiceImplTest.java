package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

class ItemServiceImplTest {
    ItemService itemService = Mockito.mock(ItemService.class);

    private final User user = new User(1L, "Linar", "Linar@xakep.ru");

    private final User userNumberTwo = new User(null, "Lenar", "Lenar@xakep.ru");

    private final ItemDto itemDtoWithoutRequest = new ItemDto(1L, "Алмазная пила", "Алмазная пила Makita",
            true, user, null);

    private final Item itemWithoutRequest = new Item(2L, "Алмазная пила", "Алмазная пила Makita",
            true, user, null);

    private final ItemDto itemDtoWithRequest = new ItemDto(1L, "Мультипекрь", "Мультипекарь Redmond",
            true, user, 3L);

    private final ItemRequest request = new ItemRequest(3L,
            "Нужна алмазная пила", user, LocalDateTime.now());

    private final Item itemWithRequest = new Item(2L, "Алмазная пила", "Алмазная пила Makita",
            true, user, request);

    private final Comment comment = new Comment(1L, "Все норм", itemWithoutRequest, userNumberTwo,
            LocalDateTime.now().minusDays(28));

    private final CommentDto commentDto = new CommentDto(1L, "Все норм", itemWithRequest, "Sasha2007",
            LocalDateTime.now().minusDays(28));

    private final ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(1L, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, user, null, null,
            null, null);

    @Test
    void addNewItemWithoutRequest() {
        Mockito
                .when(itemService.addNewItemWithoutRequest(anyLong(), any()))
                .thenReturn(itemDtoWithoutRequest);

        itemService.addNewItemWithoutRequest(1L, itemWithoutRequest);
        Mockito.verify(itemService, Mockito.times(1))
                .addNewItemWithoutRequest(1L, itemWithoutRequest);
    }

    @Test
    void addNewItemWithRequest() {
        Mockito
                .when(itemService.addNewItemWithRequest(anyLong(), any(), anyLong()))
                .thenReturn(itemDtoWithRequest);

        itemService.addNewItemWithRequest(2L, itemWithRequest, 2L);
        Mockito.verify(itemService, Mockito.times(1))
                .addNewItemWithRequest(2L, itemWithRequest, 2L);
    }

    @Test
    void editItem() {
        Item itemUpdated = new Item(1L, "Алмазная пила", "Алмазная пила",
                false, user, null);

        Mockito
                .when(itemService.editItem(anyLong(), anyLong(), any()))
                .thenReturn(itemUpdated);

        itemService.editItem(1L, 1L, itemUpdated);
        Mockito.verify(itemService, Mockito.times(1))
                .editItem(1L, 1L, itemUpdated);
    }

    @Test
    void getItem() {
        Mockito
                .when(itemService.getItem(anyLong()))
                .thenReturn(Optional.of(itemWithRequest));

        itemService.getItem(2L);
        Mockito.verify(itemService, Mockito.times(1))
                .getItem(2L);
    }

    @Test
    void getItems() {
        List<ItemDtoWithBooking> items = new ArrayList<>();
        items.add(itemDtoWithBooking);

        Mockito
                .when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        itemService.getItems(2L, 0, 10);
        Mockito.verify(itemService, Mockito.times(1))
                .getItems(2L, 0, 10);
    }

    @Test
    void getItemWithBooking() {
        Mockito
                .when(itemService.getItemWithBooking(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBooking);

        itemService.getItemWithBooking(2L, 2L);
        Mockito.verify(itemService, Mockito.times(1))
                .getItemWithBooking(2L, 2L);
    }

    @Test
    void searchItems() {
        String text = "аЛМАз";
        List<Item> items = new ArrayList<>();

        items.add(itemWithoutRequest);

        Mockito
                .when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        itemService.searchItems(1L, text, 0, 4);
        Mockito.verify(itemService, Mockito.times(1))
                .searchItems(1L, text, 0, 4);
    }

    @Test
    void addNewComment() {
        Mockito
                .when(itemService.addNewComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        itemService.addNewComment(1L, 1L, comment);
        Mockito.verify(itemService, Mockito.times(1))
                .addNewComment(1L, 1L, comment);
    }
}