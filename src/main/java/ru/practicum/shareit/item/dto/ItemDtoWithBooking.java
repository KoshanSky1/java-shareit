package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingReducedDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private BookingReducedDto lastBooking;
    private BookingReducedDto nextBooking;
    private List<CommentDto> comments;
}