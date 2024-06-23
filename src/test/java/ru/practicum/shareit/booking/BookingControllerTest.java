package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingMapper bookingMapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final User owner = new User(null, "Linar", "Linar@xakep.ru");

    private final User booker = new User(null, "Lenar", "Lenar@xakep.ru");
    private final Item item = new Item(null, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null);

    private final BookingDto bookingDto = new BookingDto(
            null,
            1L,
            LocalDateTime.of(2024, 05, 23, 23, 33, 33),
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            1L);

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2024, 05, 23, 23, 33, 33),
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            item,
            booker,
            BookingStatus.WAITING);


    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void confirmBooking() throws Exception {
        when(bookingService.confirmOrRejectBooking(anyLong(), anyInt(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsByUser() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        when(bookingService.getAllBookingsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsForAllUserThings() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        when(bookingService.getAllBookingsForAllUserThings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

}