package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingStateNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingServiceImpl.findByState;
import static ru.practicum.shareit.booking.BookingState.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private User owner = new User(null, "Sweta", "Aweta@sweta.ru");
    private User booker = new User(null, "Lenach", "Lenach@xakep.ru");
    private Item item = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
            true, owner, null);
    private Booking bookingNumberOne = new Booking(null,
            LocalDateTime.of(2024, 07, 23, 23, 33, 33),
            LocalDateTime.of(2024, 07, 28, 23, 33, 33),
            item, booker, BookingStatus.WAITING);

    private Booking bookingNumberTwo = new Booking(null,
            LocalDateTime.of(2024, 07, 29, 23, 33, 33),
            LocalDateTime.of(2024, 07, 30, 23, 33, 33),
            item, booker, BookingStatus.WAITING);

    private Booking bookingNumberThree = new Booking(null, LocalDateTime.now().plusMonths(5),
            LocalDateTime.now().plusMonths(6), item, booker, BookingStatus.WAITING);

    private Booking bookingNumberFour = new Booking(null, LocalDateTime.now().plusMonths(1),
            LocalDateTime.now().plusMonths(3), item, booker, BookingStatus.WAITING);

    @Test
    public void createBooking() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);

        Optional<Booking> bookingSaved = bookingService.getBookingById(owner.getId(), bookingNumberOne.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void createBookingWithDate() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne.setStart(LocalDateTime.of(2023, 05, 30, 23, 33, 33));
        bookingNumberOne.setEnd(LocalDateTime.of(2023, 05, 31, 23, 33, 33));

        ItemValidationException thrown = assertThrows(
                ItemValidationException.class,
                () -> bookingService.createBooking(booker.getId(), bookingNumberOne),
                "Вещь с id = [" + bookingNumberOne.getItem().getId() +
                        "] уже забронирована или даты начала и конца бронирования указаны неверно"
        );

        assertTrue(thrown.getMessage().contains("Вещь с id = [" + bookingNumberOne.getItem().getId() +
                "] уже забронирована или даты начала и конца бронирования указаны неверно"));

    }

    @Test
    public void confirmOrRejectBooking() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);

        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberOne.getId(), true);

        Optional<Booking> bookingSaved = bookingService.getBookingById(owner.getId(), bookingNumberOne.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void confirmOrRejectBookingWithFalse() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);

        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberOne.getId(), false);

        Optional<Booking> bookingSaved = bookingService.getBookingById(owner.getId(), bookingNumberOne.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void confirmOrRejectBookingByBooker() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);

        bookingService.confirmOrRejectBooking(booker.getId(), bookingNumberOne.getId(), false);

        Optional<Booking> bookingSaved = bookingService.getBookingById(owner.getId(), bookingNumberOne.getId());
        System.out.println(bookingSaved);

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void confirmOrRejectBookingWithOwner() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);

        ItemNotFoundException thrown = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.confirmOrRejectBooking(booker.getId(), bookingNumberOne.getId(), true),
                "Пользовтель с id=[" + booker.getId() + "] не является владельцем вещи с id = [" + bookingNumberOne.getId() +
                        "]"
        );

        assertTrue(thrown.getMessage().contains("Пользовтель с id=[" + booker.getId() +
                "] не является владельцем вещи с id = [" + bookingNumberOne.getId() + "]"));
    }

    @Test
    public void confirmOrRejectBookingWithApprowed() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberOne.setStatus(BookingStatus.APPROVED);

        ItemValidationException thrown = assertThrows(
                ItemValidationException.class,
                () -> bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberOne.getId(), true),
                "Бронирование уже подтверждено"
        );

        assertTrue(thrown.getMessage().contains("Бронирование уже подтверждено"));
    }

    @Test
    public void getBookingById() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);

        Optional<Booking> bookingSaved = bookingService.getBookingById(owner.getId(), bookingNumberTwo.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void getAllBookingsByUserIdWithCaseAll() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(booker.getId(), ALL, 0, 10);

        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsByUserIdWithCaseCurrent() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(booker.getId(), CURRENT, 0, 10);

        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsByUserIdWithCasePast() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(booker.getId(), PAST, 0, 10);

        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsByUserIdWithCaseFuture() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(booker.getId(), FUTURE, 0, 10);

        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsByUserIdWithCaseWaiting() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(booker.getId(), WAITING, 0, 10);

        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsByUserIdWithCaseRejected() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(booker.getId(), REJECTED, 0, 10);

        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsByUserIdWithCaseNull() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsByUserId(owner.getId(), null, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCaseAll() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), ALL, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCaseCurrent() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), CURRENT, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCasePast() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), PAST, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCaseFuture() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), FUTURE, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCaseWaiting() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), WAITING, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCaseRejected() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), REJECTED, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void getAllBookingsForAllUserThingsWithCaseNull() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberOne = bookingService.createBooking(booker.getId(), bookingNumberOne);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNumberThree);
        bookings.add(bookingNumberTwo);
        bookings.add(bookingNumberOne);

        List<Booking> bookingsActual = bookingService.getAllBookingsForAllUserThings(owner.getId(), null, 0, 10);
        assertEquals(bookings, bookingsActual);
    }

    @Test
    public void testGetBookingById() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberFour = bookingService.createBooking(booker.getId(), bookingNumberFour);

        Optional<Booking> bookingSaved = bookingService.getBookingById(owner.getId(), bookingNumberFour.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    public void testGetBookingByIdWithId() {
        owner = userService.createUser(owner);

        ItemNotFoundException thrown = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.getBookingById(owner.getId(), 77L),
                "Брониррование с id = [77] не существует"
        );

        assertTrue(thrown.getMessage().contains("Брониррование с id = [77] не существует"));
    }

    @Test
    public void findByStateTest() {
        String name = "FINALLY";

        BookingStateNotFoundException thrown = assertThrows(
                BookingStateNotFoundException.class,
                () -> findByState(name),
                "Unknown state: " + name
        );

        assertTrue(thrown.getMessage().contains("Unknown state: " + name));
    }

    @Test
    public void checkUserIngress() {
        User user = userService.createUser(new User(null, "Robert", "ggg@ggg.ru"));
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), item);
        bookingNumberFour = bookingService.createBooking(booker.getId(), bookingNumberFour);

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.checkUserIngress(user.getId(), bookingNumberFour.getId()),
                "Для пользователя с id=[1] доступ к инфо о бронировании запрещен"
        );

        assertTrue(thrown.getMessage().contains("Для пользователя с id=[" + user.getId() + "] доступ к инфо о бронировании запрещен"));
    }
}