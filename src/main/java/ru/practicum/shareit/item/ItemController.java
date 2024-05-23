package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public Item createItem(@RequestHeader(SHARER_USER_ID) long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("---START CREATE ITEM ENDPOINT---");
        return itemService.addNewItem(userId, toItem(itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@RequestHeader(SHARER_USER_ID) long userId,
                                           @PathVariable long itemId, @Valid @RequestBody ItemDto itemDto) {
        log.info("---START UPDATE ITEM ENDPOINT---");
        return new ResponseEntity<>(itemService.editItem(userId, itemId, toItem(itemDto)), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> findItem(@RequestHeader(SHARER_USER_ID) long userId,
                                         @PathVariable long itemId) {
        log.info("---START FIND ITEM BY ID ENDPOINT---");
        return new ResponseEntity<>(itemService.getItem(itemId), HttpStatus.OK);
    }

    @GetMapping
    public List<Item> findItemsByUser(@RequestHeader(SHARER_USER_ID) long userId) {
        log.info("---START FIND ITEMS BY USER ENDPOINT---");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestHeader(SHARER_USER_ID) long userId,
                                                  @RequestParam String text) {
        log.info("---START SEARCH ITEMS ENDPOINT---");
        return new ResponseEntity<>(itemService.searchItems(userId, text), HttpStatus.OK);
    }

}
