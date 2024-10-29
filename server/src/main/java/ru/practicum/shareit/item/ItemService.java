package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto addNewItemWithoutRequest(long userId, Item item);

    ItemDto addNewItemWithRequest(long userId, Item item, long requestId);

    Item editItem(long userId, long itemId, Item item);

    Optional<Item> getItem(long itemId);

    List<ItemDtoWithBooking> getItems(long ownerId, int from, int size);

    ItemDtoWithBooking getItemWithBooking(long userId, long ownerId);

    List<Item> searchItems(long userId, String text, int from, int size);

    Comment addNewComment(long userId, long itemId, Comment comment);
}