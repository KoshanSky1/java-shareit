package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserMapperTest {
    @Autowired
    private JacksonTester<UserDto> jsonNumberOne;

    @Autowired
    private JacksonTester<User> jsonNumberTwo;

    @Test
    public void toUserDto() throws IOException {
        UserDto userDto = new UserDto(
                1L,
                "Linar",
                "Linar@xakep.ru"
        );

        JsonContent<UserDto> result = jsonNumberOne.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Linar");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Linar@xakep.ru");
    }

    @Test
    public void toUser() throws IOException {
        User user = new User(
                1L,
                "Linar",
                "Linar@xakep.ru"
        );

        JsonContent<User> result = jsonNumberTwo.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Linar");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Linar@xakep.ru");
    }
}