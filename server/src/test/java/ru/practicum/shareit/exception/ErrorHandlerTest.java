package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorHandler.class)
public class ErrorHandlerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ErrorHandler errorHandler;

    @Autowired
    private MockMvc mvc;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final ErrorResponse errorResponseNumberOne = new ErrorResponse("Пользователь не найден");

    private final ErrorResponse errorResponseNumberTwo = new ErrorResponse("Вещь не найдена");

    private final ErrorResponse errorResponseNumberThree = new ErrorResponse("Запрос не найден");

    private final ErrorResponse errorResponseNumberFour = new ErrorResponse("Бронирование не найдено");

    private final ErrorResponse errorResponseNumberFive = new ErrorResponse("Вещь не прошла валидацию");

    @Test
    public void handleUserNotFoundException() throws Exception {
        when(errorHandler.handleUserNotFoundException(any()))
                .thenReturn(errorResponseNumberOne);

        mvc.perform(get("/users/111")
                        .content(mapper.writeValueAsString(errorResponseNumberOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void handleItemNotFoundException() throws Exception {
        when(errorHandler.handleUserNotFoundException(any()))
                .thenReturn(errorResponseNumberTwo);

        Response bookingDto;
        mvc.perform(get("/items/111")
                        .content(mapper.writeValueAsString(errorResponseNumberTwo))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void handleItemRequestNotFoundException() throws Exception {
        when(errorHandler.handleUserNotFoundException(any()))
                .thenReturn(errorResponseNumberThree);

        Response bookingDto;
        mvc.perform(get("/requests/111")
                        .content(mapper.writeValueAsString(errorResponseNumberThree))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void handleBookingStateNotFoundException() throws Exception {
        when(errorHandler.handleUserNotFoundException(any()))
                .thenReturn(errorResponseNumberFour);

        Response bookingDto;
        mvc.perform(get("/bookings/111")
                        .content(mapper.writeValueAsString(errorResponseNumberFour))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void handleItemValidationException() throws Exception {
        when(errorHandler.handleUserNotFoundException(any()))
                .thenReturn(errorResponseNumberFive);

        Response bookingDto;
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(errorResponseNumberFive))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isNotFound());
    }
}