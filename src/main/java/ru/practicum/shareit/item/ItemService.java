package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(long userId, Item item);

    Item editItem(long userId, long itemId, Item item);

    Item getItem(long itemId);

    List<Item> getItems(long userId);

    List<Item> searchItems(long userId, String text);
}