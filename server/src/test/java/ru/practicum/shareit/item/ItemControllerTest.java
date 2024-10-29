package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final User owner = new User(1L, "Linar", "Linar@xakep.ru");

    private final User booker = new User(2L, "Lenar", "Lenar@xakep.ru");

    private final ItemDto itemDto = new ItemDto(1L, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null);

    private final Item item = new Item(1L, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null);

    private final Comment comment = new Comment(1L, "Все норм", item, booker,
            LocalDateTime.now().minusDays(28));

    private final CommentDto commentDto = new CommentDto(1L, "Все норм", item, "Sasha2007",
            LocalDateTime.now().minusDays(28));

    private final ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(1L, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null, null,
            null, null);

    @Test
    public void createItemWithoutRequest() throws Exception {
        when(itemService.addNewItemWithoutRequest(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void createItemWithRequest() throws Exception {
        when(itemService.addNewItemWithRequest(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.editItem(anyLong(), anyLong(), any()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void findItemsByUser() throws Exception {
        List<CommentDto> commentsDto = new ArrayList<>();
        commentsDto.add(commentDto);

        itemDtoWithBooking.setComments(commentsDto);

        List<ItemDtoWithBooking> items = new ArrayList<>();
        items.add(itemDtoWithBooking);

        when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(items))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void searchItems() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(null);

        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items/search?text=мульти")
                        .content(mapper.writeValueAsString(items))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void createComment() throws Exception {
        when(itemService.addNewComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void findItem() throws Exception {
        when(itemService.getItem(anyLong()))
                .thenReturn(Optional.of(item));

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }
}