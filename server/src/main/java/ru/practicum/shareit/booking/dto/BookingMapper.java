package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemService;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemService itemService;

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }

    public Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                itemService.getItem(bookingDto.getItemId()).orElseThrow(),
                null,
                BookingStatus.WAITING
        );
    }

    public static BookingReducedDto toBookingReducedDto(Booking booking) {
        return new BookingReducedDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

}