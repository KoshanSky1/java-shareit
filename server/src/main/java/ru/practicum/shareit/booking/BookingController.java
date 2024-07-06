package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;

import java.util.List;

import static ru.practicum.shareit.booking.BookingServiceImpl.findByState;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                                 @RequestBody BookingDto bookingDto) {
        log.info("---START CREATE BOOKING ENDPOINT---");
        return new ResponseEntity<>(bookingService.createBooking(userId, bookingMapper.toBooking(bookingDto)),
                HttpStatus.OK);

    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> confirmBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                                  @PathVariable int bookingId, @RequestParam Boolean approved) {
        log.info("---START CONFIRM BOOKING ENDPOINT---");
        return new ResponseEntity<>(bookingService.confirmOrRejectBooking(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingInformation(@RequestHeader(SHARER_USER_ID) long userId,
                                                         @PathVariable int bookingId) {
        log.info("---START SEARCH GET BOOKING INFORMATION ENDPOINT---");
        bookingService.checkUserIngress(userId, bookingId);
        return new ResponseEntity<>(bookingService.getBookingById(userId, bookingId).orElseThrow(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookingsByUser(@RequestHeader(SHARER_USER_ID) long userId,
                                                              @RequestParam(defaultValue = "ALL") String state,
                                                              @RequestParam(defaultValue = "0") int from,
                                                              @RequestParam(defaultValue = "10") int size) {
        log.info("---START GET ALL BOOKINGS BY USER ENDPOINT---");
        return new ResponseEntity<>(bookingService.getAllBookingsByUserId(userId, findByState(state), from, size),
                HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Booking>> getAllBookingsForAllUserThings(@RequestHeader(SHARER_USER_ID) long userId,
                                                                        @RequestParam(defaultValue = "ALL") String state,
                                                                        @RequestParam(defaultValue = "0") int from,
                                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("---START GET ALL BOOKINGS BY OWNER ENDPOINT---");
        return new ResponseEntity<>(bookingService.getAllBookingsForAllUserThings(userId, findByState(state), from, size),
                HttpStatus.OK);
    }

}