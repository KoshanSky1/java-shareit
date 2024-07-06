package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemMapperTest {
    @Autowired
    private JacksonTester<ItemDto> jsonNumberOne;

    @Autowired
    private JacksonTester<Item> jsonNumberTwo;

    @Autowired
    private JacksonTester<Comment> jsonNumberThree;

    @Autowired
    private JacksonTester<CommentDto> jsonNumberFour;

    private final User owner = new User(1L, "Linar", "Linar@xakep.ru");

    private final User booker = new User(2L, "Lenar", "Lenar@xakep.ru");

    private final Item item = new Item(1L, "Алмазная пила", "Алмазная пила Makita",
            true, owner, null);

    @Test
    public void toItemDto() throws IOException {
        ItemDto itemDto = new ItemDto(
                2L,
                item.getName(),
                item.getDescription(),
                true,
                owner,
                null
        );

        JsonContent<ItemDto> result = jsonNumberOne.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Алмазная пила");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Алмазная пила Makita");
        assertThat(result).extractingJsonPathStringValue("$.avaliable").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
    }

    @Test
    public void toItem() throws IOException {
        Item item = new Item(1L, "Алмазная пила", "Алмазная пила Makita",
                true, owner, null);

        JsonContent<Item> result = jsonNumberTwo.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Алмазная пила");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Алмазная пила Makita");
        assertThat(result).extractingJsonPathStringValue("$.avaliable").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
    }

    @Test
    public void toComment() throws IOException {
        Comment comment = new Comment(1L, "Все норм", item, booker,
                LocalDateTime.of(2024, 07, 23, 23, 33, 33));

        JsonContent<Comment> result = jsonNumberThree.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Все норм");
        assertThat(result).extractingJsonPathStringValue("$.booker").isEqualTo(null);
    }

    @Test
    public void toCommentDto() throws IOException {
        CommentDto commentDto = new CommentDto(1L, "Все норм", item, booker.getName(),
                LocalDateTime.of(2024, 07, 23, 23, 33, 33));

        JsonContent<CommentDto> result = jsonNumberFour.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Все норм");
        assertThat(result).extractingJsonPathStringValue("$.booker").isEqualTo(null);
    }
}