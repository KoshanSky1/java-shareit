package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class BookingServiceImplDataBaseTest {
    private final UserServiceImpl userService;

    private final ItemServiceImpl itemService;

    private final BookingServiceImpl bookingService;

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
    void getAllBookingsByUserId() {
        userService.createUser(owner);
        userService.createUser(booker);

        itemService.addNewItemWithoutRequest(owner.getId(), item);

        bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.createBooking(booker.getId(), bookingNumberThree);

        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberOne.getId(), true);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), false);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberThree.getId(), true);

        List<Booking> bookings = bookingService.getAllBookingsByUserId(booker.getId(), BookingState.ALL, 0, 20);

        List<Booking> bookingsExp = new ArrayList<>();

        bookingsExp.add(bookingNumberThree);
        bookingsExp.add(bookingNumberTwo);
        bookingsExp.add(bookingNumberOne);

        assertEquals(bookingsExp.size(), bookings.size());

        assertEquals(bookingsExp.get(0).getId(), bookings.get(0).getId());
        assertEquals(bookingsExp.get(1).getId(), bookings.get(1).getId());
        assertEquals(bookingsExp.get(2).getId(), bookings.get(2).getId());
    }
}