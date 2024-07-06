package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoWithoutOwner;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDtoWithAnswers toItemRequestDtoWithAnswers(ItemRequest request,
                                                                        List<ItemDtoWithoutOwner> answers) {
        return new ItemRequestDtoWithAnswers(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                answers
        );
    }
}