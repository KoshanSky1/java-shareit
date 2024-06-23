package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequest addNewItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithAnswers> findItemRequestsByUserId(long requestorId);

    List<ItemRequestDtoWithAnswers> getAllItemRequests(long requestorId, int from, int size);

    ItemRequestDtoWithAnswers getItemRequestById(long userId, long requestId);

    Optional<ItemRequest> findItemRequestById(long requestId);
}
