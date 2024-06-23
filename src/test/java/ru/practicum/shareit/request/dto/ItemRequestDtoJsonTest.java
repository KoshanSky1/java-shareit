package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoWithoutOwner;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDtoWithAnswers> json;

    User owner = new User(1L, "Linar", "Linar@xakep.ru");

    User requestor = new User(2L, "Lenar", "Lenar@xakep.ru");

    ItemRequest request = new ItemRequest(1L,
            "Нужна алмазная пила", requestor, LocalDateTime.of(2024, 05, 23, 23,
            33, 33));

    ItemDtoWithoutOwner item = new ItemDtoWithoutOwner(1L, "Алмазная пила", "Алмазная пила Makita",
            true, 1L);

    @Test
    void testItemRequestDtoWithAnswers() throws Exception {
        List<ItemDtoWithoutOwner> items = new ArrayList<>();
        items.add(item);

        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(
                1L,
                "Нужна газонокосилка",
                LocalDateTime.of(2024, 05, 23, 23, 33, 33),
                items);

        JsonContent<ItemRequestDtoWithAnswers> result = json.write(itemRequestDtoWithAnswers);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна газонокосилка");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-05-23T23:33:33");
    }
}