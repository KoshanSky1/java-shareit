package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BookingStateNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static ru.practicum.shareit.booking.BookingState.ALL;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;

    @Override
    public Booking createBooking(long userId, Booking booking) {

        if (userId == booking.getItem().getId()) {
            throw new ItemNotFoundException("Id пользователя и владельца вещи совпадают");
        }

        if (booking.getItem().getAvailable() && checkDate(booking.getStart(), booking.getEnd())) {
            booking.setBooker(userService.getUser(userId).orElseThrow());
            log.info(format("Созданo бронирование: %s", booking));
            return repository.save(booking);
        } else {
            throw new ItemValidationException(format("Вещь с id = [%s] уже забронирована " +
                    "или даты начала и конца бронирования указаны неверно", booking.getItem().getId()));
        }
    }

    @Override
    public Booking confirmOrRejectBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingById(userId, bookingId).orElseThrow();

        if (approved) {
            if (getBookingById(userId, bookingId).orElseThrow().getItem().getOwner().getId() != userId) {
                throw new ItemNotFoundException(format("Пользовтель с id=[%s] не является владельцем вещи с id = [%s]",
                        userId, bookingId));
            }
            ;
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                throw new ItemNotFoundException("Дата начала бронирования уже в прошлом");
            }
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ItemValidationException("Бронирование уже подтверждено");
            }
            booking.setStatus(BookingStatus.APPROVED);
            repository.save(booking);
            log.info(format("Бронирование с id=[%s] подтверждено", booking));
        } else {
            booking = getBookingById(userId, bookingId).orElseThrow();
            if (booking.getBooker().getId() == userId) {
                booking.setStatus(BookingStatus.CANCELED);
                repository.save(booking);
                log.info(format("Бронирование с id=[%s] отменено пользователем", booking));
            } else {
                booking.setStatus(BookingStatus.REJECTED);
                repository.save(booking);
                log.info(format("Бронирование с id=[%s] отклонено владельцем", booking));
            }
        }
        return booking;
    }

    @Override
    public Optional<Booking> getBookingById(long userId, long bookingId) {

        repository.findById(bookingId);
        if (repository.findById(bookingId).isEmpty()) {
            throw new ItemNotFoundException(format("Брониррование с id = [%s] не существует", bookingId));
        }
        log.info(format("Найдено бронирование с id=[%s]", bookingId));
        return repository.findById(bookingId);
    }

    @Override
    public List<Booking> getAllBookingsByUserId(long userId, BookingState state, int from, int size) {
        userService.getUser(userId);
        List<Booking> bookings = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (from < 0 || size <= 0) {
            throw new ItemValidationException("Индекс первого элемента не может быть отрицательным," +
                    " количество элементов для отображения должно быть больше 0");
        }

        if (state == null) {
            state = ALL;
        }

        switch (state) {
            case ALL:
                bookings = repository.findByBooker_Id(userId);
                break;
            case CURRENT:
                bookings = repository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = repository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = repository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = repository.findByBooker_Id(userId);
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        log.info(format("Сформирован список бронирований для пользователя id=[%s], state=[%s]", userId, state));

        return pagedResponse(bookings, from, size);
    }

    @Override
    public List<Booking> getAllBookingsForAllUserThings(long ownerId, BookingState state, int from, int size) {
        userService.getUser(ownerId);
        List<Booking> bookings = new ArrayList<>();

        if (from < 0 || size <= 0) {
            throw new ItemValidationException("Индекс первого элемента не может быть отрицательным," +
                    " количество элементов для отображения должно быть больше 0");
        }

        if (state == null) {
            state = ALL;
        }

        switch (state) {
            case ALL:
                bookings = repository.findByOwner_Id(ownerId);
                break;
            case CURRENT:
                bookings = repository.findByOwner_IdAndStartIsBeforeAndEndIsAfter(ownerId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = repository.findByOwner_IdAndEndIsBefore(ownerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.findBOwner_IdAndStartIsAfter(ownerId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = repository.findByOwner_IdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByOwner_IdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                bookings = repository.findByOwner_Id(ownerId);
        }
        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        log.info(format("Сформирован список бронирований для владельца вещей id=[%s], state=[%s]", ownerId, state));

        return pagedResponse(bookings, from, size);
    }

    @Override
    public void checkUserIngress(long userId, long bookingId) {
        Booking booking = getBookingById(userId, bookingId).orElseThrow();

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UserNotFoundException(format("Для пользователя с id=[%s] доступ к инфо о бронировании запрещен",
                    userId));
        }
    }

    private Boolean checkDate(LocalDateTime start, LocalDateTime end) {
        return !end.isBefore(LocalDateTime.now()) && !start.isBefore(LocalDateTime.now()) && !end.equals(start)
                && !start.isAfter(end);
    }

    public static BookingState findByState(String name) {
        for (BookingState state : BookingState.values()) {
            if (name.equalsIgnoreCase(state.name())) {
                return state;
            }
        }
        throw new BookingStateNotFoundException("Unknown state: " + name);
    }

    private List<Booking> pagedResponse(List<Booking> bookings, int from, int size) {
        int totalBookings = bookings.size();
        int toIndex = from + size;

        if (from <= totalBookings) {
            if (toIndex > totalBookings) {
                toIndex = totalBookings;
            }
            return bookings.subList(from, toIndex);
        } else {
            return Collections.emptyList();
        }
    }

}