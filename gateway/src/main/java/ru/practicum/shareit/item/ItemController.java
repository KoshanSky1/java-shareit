package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARER_USER_ID) long userId,
                                             @RequestBody @Valid ShortItemDto shortItemDto) {
        log.info("---START CREATE ITEM ENDPOINT---");
        return itemClient.createItem(userId, shortItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID) long userId,
                                             @PathVariable long itemId, @Valid @RequestBody ItemDto itemDto) {
        log.info("---START UPDATE ITEM ENDPOINT---");
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(SHARER_USER_ID) long userId,
                                          @PathVariable long itemId) {
        log.info("---START FIND ITEM BY ID ENDPOINT---");
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(SHARER_USER_ID) long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("---START FIND ITEMS BY USER ENDPOINT---");
        return itemClient.getItemsByUser(userId, from, size);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(SHARER_USER_ID) long userId,
                                              @RequestParam(name = "text") String textParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("---START SEARCH ITEMS ENDPOINT---");
        return itemClient.searchItems(userId, textParam, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(SHARER_USER_ID) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("---START CREATE COMMENT ENDPOINT---");
        return itemClient.createComment(userId, itemId, commentDto);
    }

}