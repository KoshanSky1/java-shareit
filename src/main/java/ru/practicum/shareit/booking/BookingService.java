package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(long userId, Booking booking);

    Booking confirmOrRejectBooking(long userId, long bookingId, boolean approved);

    Optional<Booking> getBookingById(long userId, long bookingId);

    List<Booking> getAllBookingsByUserId(long userId, BookingState state);

    List<Booking> getAllBookingsForAllUserThings(long userId, BookingState state);

    void checkUserIngress(long userId, long bookingId);
}
