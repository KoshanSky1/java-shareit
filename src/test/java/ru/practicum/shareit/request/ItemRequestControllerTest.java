package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoWithoutOwner;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestMapper itemRequestMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final User userNumberOne = new User(null, "Linar", "Linar@xakep.ru");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Нужна газонокосилка",
            userNumberOne,
            LocalDateTime.of(2024, 06, 23, 23, 33, 33));

    private final ItemRequest itemRequest = new ItemRequest(
            1L,
            "Нужна газонокосилка",
            userNumberOne,
            LocalDateTime.of(2024, 06, 23, 23, 33, 33));

    private List<ItemDtoWithoutOwner> items = new ArrayList<>();

    private final ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(
            1L,
            "Нужна газонокосилка",
            LocalDateTime.of(2024, 06, 23, 23, 33, 33),
            items);

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.addNewItemRequest(anyLong(), any()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void findItemRequestsByUserId() throws Exception {
        List<ItemRequestDtoWithAnswers> itemsRequestDtoWithAnswers = new ArrayList<>();
        itemsRequestDtoWithAnswers.add(itemRequestDtoWithAnswers);

        when(itemRequestService.findItemRequestsByUserId(anyLong()))
                .thenReturn(itemsRequestDtoWithAnswers);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoWithAnswers))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void findAllItemRequests() throws Exception {
        List<ItemRequestDtoWithAnswers> itemsRequestDtoWithAnswers = new ArrayList<>();
        itemsRequestDtoWithAnswers.add(itemRequestDtoWithAnswers);

        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemsRequestDtoWithAnswers);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDtoWithAnswers))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void findItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyInt()))
                .thenReturn(itemRequestDtoWithAnswers);


        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }
}