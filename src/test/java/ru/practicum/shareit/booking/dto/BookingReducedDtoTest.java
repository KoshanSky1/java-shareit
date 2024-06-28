package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingReducedDtoTest {
    @Autowired
    private JacksonTester<BookingReducedDto> json;

    private final User booker = new User(1L, "Lenar", "Lenar@xakep.ru");

    @Test
    void toBookingReducedDto() throws IOException {
        BookingReducedDto bookingReducedDto = new BookingReducedDto(
                1L,
                booker.getId(),
                LocalDateTime.of(2024, 05, 23, 23, 33, 33),
                LocalDateTime.of(2024, 07, 23, 23, 33, 33)
        );

        JsonContent<BookingReducedDto> result = json.write(bookingReducedDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-05-23T23:33:33");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-07-23T23:33:33");

    }

}