package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Item addNewItem(long userId, Item item) {
        log.info(format("Создан предмет: %s", item));
        return itemRepository.createItem(userId, item);
    }

    @Override
    public Item editItem(long userId, long itemId, Item item) {
        log.info(format("Обновлён предмет: %s", itemId));
        return itemRepository.updateItem(userId, itemId, item);
    }

    @Override
    public Item getItem(long itemId) {
        log.info(format("Найден предмет с id= %s", itemId));
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getItems(long userId) {
        log.info(format("Сформирован список предметов для пользователя с id= %s", userId));
        return itemRepository.getItemsByUser(userId);
    }

    @Override
    public List<Item> searchItems(long userId, String text) {
        log.info(format("Сформирован список предметов, содержащих текст %s", text));
        return itemRepository.searchItems(userId, text);
    }
}
