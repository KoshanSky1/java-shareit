package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemValidationException;
import ru.practicum.shareit.user.UserRepositoryImpl;
import ru.practicum.shareit.user.model.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.format;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final UserRepositoryImpl userRepository;
    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item createItem(long userId, Item item) {
        if (userRepository.getUser(userId) == null) {
            throw new UserNotFoundException(format("Пользователь с id= [%s] не существует", userId));
        }
        if (item.getName().isBlank() || item.getDescription() == null || item.getAvailable() == null) {
            throw new ItemValidationException("Не заполнено одно из обязательных полей: имя, описание или статус");
        }
        id++;
        item.setId(id);
        item.setOwner(userRepository.getUser(userId));
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        Item itemUpdated = items.get(itemId);

        if (!itemUpdated.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователи не совпадают");
        }
        if (item.getName() != null) {
            itemUpdated.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpdated.setDescription(item.getDescription());
        }
        if (item.getAvailable() != itemUpdated.getAvailable() && item.getAvailable() != null) {
            itemUpdated.setAvailable(item.getAvailable());
        }

        return itemUpdated;
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUser(long userId) {
        List<Item> userItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> searchItems(long userId, String text) {
        List<Item> foundItems = new ArrayList<>();

        if (userRepository.getUser(userId) == null) {
            throw new UserNotFoundException(format("Пользователь с id= [%s] не существует", userId));
        }
        if (text.isEmpty()) {
            return foundItems;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable()) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }

}
