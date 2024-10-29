package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingReducedDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDtoWithBooking> jsonNumberOne;

    @Autowired
    private JacksonTester<ItemDtoWithoutOwner> jsonNumberTwo;

    @Autowired
    private JacksonTester<Comment> jsonNumberThree;

    @Autowired
    private JacksonTester<CommentDto> jsonNumberFour;

    private final User owner = new User(1L, "Linar", "Linar@xakep.ru");

    private final User booker = new User(2L, "Lenar", "Lenar@xakep.ru");

    private final Item item = new Item(null, "Алмазная пила", "Алмазная пила Makita",
            true, owner, null);

    private final BookingReducedDto bookingNumberOne = new BookingReducedDto(1L, booker.getId(), LocalDateTime.now().minusMonths(3),
            LocalDateTime.now().minusMonths(2));

    private final BookingReducedDto bookingNumberTwo = new BookingReducedDto(1L, booker.getId(), LocalDateTime.now().minusMonths(3),
            LocalDateTime.now().minusMonths(2));

    private final CommentDto comment = new CommentDto(1L, "Все норм", item, booker.getName(), LocalDateTime.now().minusDays(28));

    @Test
    public void toItemDtoWithBooking() throws IOException {
        List<CommentDto> comments = new ArrayList<>();
        comments.add(comment);

        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(
                1L,
                item.getName(),
                item.getDescription(),
                true,
                owner,
                null,
                bookingNumberOne,
                bookingNumberTwo,
                comments);

        JsonContent<ItemDtoWithBooking> result = jsonNumberOne.write(itemDtoWithBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Алмазная пила");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Алмазная пила Makita");
        assertThat(result).extractingJsonPathStringValue("$.avaliable").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.request").isEqualTo(null);
    }

    @Test
    public void toItemDtoWithoutOwner() throws IOException {
        ItemDtoWithoutOwner itemDtoWithoutOwner = new ItemDtoWithoutOwner(
                1L,
                item.getName(),
                item.getDescription(),
                true,
                1L
        );

        JsonContent<ItemDtoWithoutOwner> result = jsonNumberTwo.write(itemDtoWithoutOwner);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Алмазная пила");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Алмазная пила Makita");
        assertThat(result).extractingJsonPathStringValue("$.avaliable").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
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