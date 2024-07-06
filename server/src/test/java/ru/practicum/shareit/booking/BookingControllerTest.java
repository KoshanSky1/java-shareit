package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final User owner = new User(1L, "Linar", "Linar@xakep.ru");

    private final User booker = new User(2L, "Lenar", "Lenar@xakep.ru");
    private final Item item = new Item(1L, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null);

    private final BookingDto bookingDto = new BookingDto(
            1L, item.getId(),
            LocalDateTime.of(2024, 05, 23, 23, 33, 33),
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            booker.getId());

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2024, 05, 23, 23, 33, 33),
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            item,
            booker,
            BookingStatus.WAITING);

    @Test
    public void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is("2024-05-23T23:33:33")))
                .andExpect(jsonPath("$.end", is("2024-06-23T23:33:33")))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    public void confirmBooking() throws Exception {
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
    public void getAllBookingsByUser() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        when(bookingService.getAllBookingsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllBookingsForAllUserThings() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        when(bookingService.getAllBookingsForAllUserThings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void getBookingInformation() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }
}