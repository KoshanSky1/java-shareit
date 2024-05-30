package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    Item getItemById(long itemId);

    List<Item> getItemsByUser(long userId);

    List<Item> searchItems(long userId, String text);
}