package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import javax.validation.Valid;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequest createItemRequest(@RequestHeader(SHARER_USER_ID) long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("---START CREATE ITEM REQUEST ENDPOINT---");
        return itemRequestService.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> findItemRequestsByUserId(@RequestHeader(SHARER_USER_ID) long requestorId) {
        log.info("---START FIND ITEM REQUESTS BY REQUESTOR ENDPOINT---");
        return itemRequestService.findItemRequestsByUserId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> findAllItemRequests(@RequestHeader(SHARER_USER_ID) long requestorId,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("---START FIND ALL ITEM REQUESTS ENDPOINT---");
        return itemRequestService.getAllItemRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers findItemRequestById(@RequestHeader(SHARER_USER_ID) long userId,
                                                         @PathVariable long requestId) {
        log.info("---START FIND ITEM REQUEST BY ID ENDPOINT---");
        return itemRequestService.getItemRequestById(userId, requestId);
    }

}