package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(SHARER_USER_ID) long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("---START CREATE ITEM ENDPOINT---");
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(SHARER_USER_ID) long userId) {
        log.info("---START GET ITEM REQUESTS BY REQUESTOR ENDPOINT---");
        return requestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader(SHARER_USER_ID) long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("---START GET ALL ITEM REQUESTS ENDPOINT---");
        return requestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestsById(@RequestHeader(SHARER_USER_ID) long userId,
                                                      @PathVariable long requestId) {
        log.info("---START GET ITEM REQUESTS BY ID ENDPOINT---");
        return requestClient.getItemRequestsById(userId, requestId);
    }

}