package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Item addNewItem(long userId, Item item);

    Item editItem(long userId, long itemId, Item item);

    Optional<Item> getItem(long itemId);

    List<ItemDtoWithBooking> getItems(long ownerId);

    ItemDtoWithBooking getItemWithBooking(long userId, long ownerId);

    List<Item> searchItems(long userId, String text);

    Comment addNewComment(long userId, long itemId, Comment comment);
}