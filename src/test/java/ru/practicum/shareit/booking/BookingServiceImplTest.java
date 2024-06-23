package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.BookingStateNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static ru.practicum.shareit.booking.BookingServiceImpl.findByState;
import static ru.practicum.shareit.booking.BookingState.ALL;

class BookingServiceImplTest {
    BookingService bookingService = Mockito.mock(BookingService.class);

    private final User owner = new User(null, "Linar", "Linar@xakep.ru");

    private final User booker = new User(null, "Lenar", "Lenar@xakep.ru");
    private final Item item = new Item(null, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null);

    private final Booking bookingNumberOne = new Booking(null, LocalDateTime.now().plusMonths(1),
            LocalDateTime.now().plusMonths(2), item, booker, BookingStatus.WAITING);

    private final Booking bookingNumberTwo = new Booking(null, LocalDateTime.now().plusMonths(3),
            LocalDateTime.now().plusMonths(4), item, booker, BookingStatus.WAITING);

    private final Booking bookingNumberThree = new Booking(null, LocalDateTime.now().plusMonths(5),
            LocalDateTime.now().plusMonths(6), item, booker, BookingStatus.WAITING);

    @Test
    void createBooking() {
        Mockito
                .when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(bookingNumberOne);

        bookingService.createBooking(1L, bookingNumberOne);
        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(1L, bookingNumberOne);
    }

    @Test
    void confirmOrRejectBooking() {
        Mockito
                .when(bookingService.confirmOrRejectBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingNumberOne);

        bookingService.confirmOrRejectBooking(1L, 1L, false);
        Mockito.verify(bookingService, Mockito.times(1))
                .confirmOrRejectBooking(1L, 1L, false);
    }

    @Test
    void getBookingById() {
        Mockito
                .when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(Optional.of(bookingNumberOne));

        bookingService.getBookingById(1L, 1L);
        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingById(1L, 1L);
    }

    @Test
    void getAllBookingsByUserId() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberOne);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberThree);

        Mockito
                .when(bookingService.getAllBookingsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        bookingService.getAllBookingsByUserId(1L, ALL, 0, 10);
        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsByUserId(1L, ALL, 0, 10);
    }

    @Test
    void getAllBookingsForAllUserThings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberOne);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberThree);

        Mockito
                .when(bookingService.getAllBookingsForAllUserThings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        bookingService.getAllBookingsForAllUserThings(1L, ALL, 0, 10);
        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsForAllUserThings(1L, ALL, 0, 10);
    }

    @Test
    void testGetBookingById() {
        Mockito
                .when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(Optional.of(bookingNumberOne));
    }

    @Test
    void findByStateTest() {
        String name = "FINALLY";

        BookingStateNotFoundException thrown = assertThrows(
                BookingStateNotFoundException.class,
                () -> findByState(name),
                "Unknown state: " + name
        );
        assertTrue(thrown.getMessage().contains("Unknown state: " + name));
    }
}