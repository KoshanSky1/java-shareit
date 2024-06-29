package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingReducedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor
public class ItemMapper {

    private final ItemRequestService itemRequestService;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                getLongValueOrDefault(item.getRequest())
        );
    }


    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                null
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item, BookingReducedDto lastBooking,
                                                          BookingReducedDto nextBooking, List<CommentDto> comments) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                getLongValueOrDefault(item.getRequest()),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static ItemDtoWithoutOwner toItemDtoWithoutOwner(Item item) {
        return new ItemDtoWithoutOwner(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                getLongValueOrDefault(item.getRequest())
        );
    }

    public static Comment toComment(CommentDto commentDto, User author) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItem(),
                author,
                commentDto.getCreated()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    private static Long getLongValueOrDefault(ItemRequest value) {
        if (value == null) {
            return null;
        } else {
            return value.getId();
        }
    }

}