package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final User owner = new User(null, "Linar", "Linar@xakep.ru");

    private final User booker = new User(null, "Lenar", "Lenar@xakep.ru");
    private final Item item = new Item(null, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null);

    private final Booking bookingNumberOne = new Booking(null, LocalDateTime.now().minusMonths(1),
            LocalDateTime.now().plusMonths(1), item, booker, BookingStatus.WAITING);

    private final Booking bookingNumberTwo = new Booking(null, LocalDateTime.now().minusMonths(2),
            LocalDateTime.now().minusMonths(1), item, booker, BookingStatus.WAITING);

    private final Booking bookingNumberThree = new Booking(null, LocalDateTime.now().plusMonths(4),
            LocalDateTime.now().plusMonths(5), item, booker, BookingStatus.WAITING);

    @BeforeEach
    void beforeEach() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        repository.save(bookingNumberOne);
        repository.save(bookingNumberTwo);
        repository.save(bookingNumberThree);
    }

    @Test
    void findByOwner_Id() {

        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberOne);
        expectedBookings.add(bookingNumberTwo);
        expectedBookings.add(bookingNumberThree);

        List<Booking> actualBookings = repository.findByOwner_Id(owner.getId());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwner_IdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberOne);

        List<Booking> actualBookings = repository.findByOwner_IdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                LocalDateTime.now(), LocalDateTime.now());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwner_IdAndEndIsBefore() {
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberTwo);

        List<Booking> actualBookings = repository.findByOwner_IdAndEndIsBefore(owner.getId(),
                LocalDateTime.now());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findBOwner_IdAndStartIsAfter() {
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberThree);

        List<Booking> actualBookings = repository.findBOwner_IdAndStartIsAfter(owner.getId(),
                LocalDateTime.now());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwner_IdAndStatusWaiting() {
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberOne);
        expectedBookings.add(bookingNumberTwo);
        expectedBookings.add(bookingNumberThree);

        List<Booking> actualBookings = repository.findByOwner_IdAndStatus(owner.getId(), BookingStatus.WAITING);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwner_IdAndStatusApproved() {
        bookingNumberOne.setStatus(BookingStatus.APPROVED);

        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberOne);

        List<Booking> actualBookings = repository.findByOwner_IdAndStatus(owner.getId(), BookingStatus.APPROVED);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwner_IdAndStatusCanceled() {
        bookingNumberThree.setStatus(BookingStatus.CANCELED);

        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberThree);

        List<Booking> actualBookings = repository.findByOwner_IdAndStatus(owner.getId(), BookingStatus.CANCELED);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwner_IdAndStatusRejected() {
        bookingNumberTwo.setStatus(BookingStatus.REJECTED);

        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingNumberTwo);

        List<Booking> actualBookings = repository.findByOwner_IdAndStatus(owner.getId(), BookingStatus.REJECTED);

        assertEquals(expectedBookings, actualBookings);
    }

}