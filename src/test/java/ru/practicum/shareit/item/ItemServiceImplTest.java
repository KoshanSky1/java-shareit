package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService requestService;
    private final BookingService bookingService;

    private User owner = new User(null, "Sweta", "Aweta@sweta.ru");
    private final Item itemWithoutRequest = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
            true, owner, null);

    private User requestor = new User(null, "Lenar", "Lenar@xakep.ru");

    private final ItemRequestDto requestDto = new ItemRequestDto(1L,
            "Нужна алмазная пила", requestor, LocalDateTime.now());

    private ItemRequest request = new ItemRequest(1L,
            "Нужна алмазная пила", requestor, LocalDateTime.now());

    private final Item itemWithRequest = new Item(null, "Мультипекарь", "Мультипекарь Redmond со сменными панелями",
            true, owner, request);

    private Item itemUpdated = new Item(null, "Алмазная пила", "Алмазная пила",
            false, owner, null);

    private final ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(1L, "Мультипекарь",
            "Мультипекарь Redmond со сменными панелями", true, owner, null, null,
            null, null);

    private User booker = new User(null, "Lenach", "Lenach@xakep.ru");

    private Booking bookingNumberOne = new Booking(null,
            LocalDateTime.of(2024, 05, 23, 23, 33, 33),
            LocalDateTime.of(2024, 06, 28, 23, 33, 33),
            itemWithoutRequest, booker, BookingStatus.WAITING);

    private Booking bookingNumberTwo = new Booking(null,
            LocalDateTime.of(2024, 07, 29, 23, 33, 33),
            LocalDateTime.of(2024, 07, 30, 23, 33, 33),
            itemWithoutRequest, booker, BookingStatus.WAITING);

    private Booking bookingNumberThree = new Booking(null, LocalDateTime.now().plusMonths(5),
            LocalDateTime.now().plusMonths(6), itemWithoutRequest, booker, BookingStatus.WAITING);

    private final Comment comment = new Comment(null, "Все норм", itemWithoutRequest, booker,
            LocalDateTime.now().minusDays(28));

    private final CommentDto commentDto = new CommentDto(1L, "Все норм", itemWithoutRequest, "Sasha2007",
            LocalDateTime.now().minusDays(28));

    @Test
    void addNewItemWithoutRequest() {
        owner = userService.createUser(owner);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        Optional<Item> itemSaved = itemService.getItem(itemDto.getId());

        assertTrue(itemSaved.isPresent());
    }

    @Test
    void addNewItemWithoutRequestAndNull() {
        owner = userService.createUser(owner);
        requestor = userService.createUser(requestor);
        request = requestService.addNewItemRequest(requestor.getId(), requestDto);
        itemWithoutRequest.setDescription(null);

        ItemValidationException thrown = assertThrows(
                ItemValidationException.class,
                () -> itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest),
                "Не заполнено одно из обязательных полей: имя, описание или статус"
        );

        assertTrue(thrown.getMessage().contains("Не заполнено одно из обязательных полей: имя, описание или статус"));
    }

    @Test
    void addNewItemWithRequest() {
        owner = userService.createUser(owner);
        requestor = userService.createUser(requestor);
        request = requestService.addNewItemRequest(requestor.getId(), requestDto);
        itemWithRequest.setDescription(null);

        ItemValidationException thrown = assertThrows(
                ItemValidationException.class,
                () -> itemService.addNewItemWithRequest(owner.getId(), itemWithRequest, request.getId()),
                "Не заполнено одно из обязательных полей: имя, описание или статус"
        );

        assertTrue(thrown.getMessage().contains("Не заполнено одно из обязательных полей: имя, описание или статус"));
    }

    @Test
    void editItem() {
        owner = userService.createUser(owner);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        Item savedItem = itemService.editItem(owner.getId(), itemDto.getId(), itemUpdated);

        assertEquals(itemUpdated.getName(), savedItem.getName());
        assertEquals(itemUpdated.getDescription(), savedItem.getDescription());
    }

    @Test
    void editItemWithUser() {
        booker = userService.createUser(booker);
        owner = userService.createUser(owner);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        Item savedItem = itemService.editItem(owner.getId(), itemDto.getId(), itemUpdated);

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> itemService.editItem(booker.getId(), itemDto.getId(), itemUpdated),
                "Пользователи не совпадают"
        );
        System.out.println(thrown.getMessage());
        assertTrue(thrown.getMessage().equals("Пользователи не совпадают"));
    }

    @Test
    void getItems() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        Booking booking = new Booking(null,
                LocalDateTime.of(2024, 07, 29, 23, 33, 33),
                LocalDateTime.of(2024, 07, 30, 23, 33, 33),
                toItem(itemDto), booker, BookingStatus.WAITING);
        booking = bookingService.createBooking(booker.getId(), booking);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);
        bookingNumberTwo.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberTwo.setEnd(LocalDateTime.of(2024, 05, 29, 23, 33, 33));
        bookingService.confirmOrRejectBooking(owner.getId(), booking.getId(), true);

        List<ItemDtoWithBooking> items = new ArrayList<>();
        items.add(itemDtoWithBooking);

        List<ItemDtoWithBooking> itemsActual = itemService.getItems(owner.getId(), 0, 10);

        assertEquals(items.size(), itemsActual.size());

    }

    @Test
    void getItemsWithLastBooking() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);
        bookingNumberTwo.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberTwo.setEnd(LocalDateTime.of(2024, 05, 29, 23, 33, 33));

        List<ItemDtoWithBooking> items = new ArrayList<>();
        items.add(itemDtoWithBooking);

        List<ItemDtoWithBooking> itemsActual = itemService.getItems(owner.getId(), 0, 10);

        assertEquals(items.size(), itemsActual.size());

    }

    @Test
    void getItemsWithSize() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        Booking booking = new Booking(null,
                LocalDateTime.of(2024, 07, 29, 23, 33, 33),
                LocalDateTime.of(2024, 07, 30, 23, 33, 33),
                toItem(itemDto), booker, BookingStatus.WAITING);
        booking = bookingService.createBooking(booker.getId(), booking);


        List<ItemDtoWithBooking> itemsActual = itemService.getItems(owner.getId(), 7, 10);

        assertTrue(itemsActual.isEmpty());

    }


    @Test
    void getItemWithBooking() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberThree.getId(), true);

        List<CommentDto> comments = new ArrayList<>();
        itemDtoWithBooking.setComments(comments);

        ItemDtoWithBooking itemDtoWithBookings = itemService.getItemWithBooking(owner.getId(), itemDto.getId());
        System.out.println(itemDtoWithBookings);
        assertEquals(itemDtoWithBookings.getName(), itemDtoWithBooking.getName());
    }

    @Test
    void getItemWithLastBooking() {

        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);
        bookingNumberTwo.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberTwo.setEnd(LocalDateTime.of(2024, 05, 29, 23, 33, 33));

        List<CommentDto> comments = new ArrayList<>();
        itemDtoWithBooking.setComments(comments);

        ItemDtoWithBooking itemDtoWithBookings = itemService.getItemWithBooking(owner.getId(), itemDto.getId());

        assertEquals(itemDtoWithBookings.getName(), itemDtoWithBooking.getName());
    }

    @Test
    void searchItems() {
        String text = "мульти";
        owner = userService.createUser(owner);
        User user = userService.createUser(requestor);

        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        List<Item> items = new ArrayList<>();
        items.add(itemWithoutRequest);

        List<Item> itemsActual = itemService.searchItems(owner.getId(), text, 0, 10);

        assertEquals(items, itemsActual);
    }

    @Test
    void searchItemsWithSize() {
        String text = "мульти";
        owner = userService.createUser(owner);
        User user = userService.createUser(requestor);

        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        List<Item> itemsActual = itemService.searchItems(owner.getId(), text, 7, 10);

        assertTrue(itemsActual.isEmpty());
    }


    @Test
    void searchItemsWithEmptyText() {
        String text = "";
        owner = userService.createUser(owner);
        User user = userService.createUser(requestor);

        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        List<Item> items = new ArrayList<>();

        List<Item> itemsActual = itemService.searchItems(owner.getId(), text, 0, 10);

        assertTrue(itemsActual.isEmpty());
    }

    @Test
    void getItem() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);

        Optional<Item> bookingSaved = itemService.getItem(itemDto.getId());

        assertTrue(bookingSaved.isPresent());
    }

    @Test
    void getItemWithId() {

        ItemNotFoundException thrown = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItem(77L),
                "Вещь с id = [77] не существует"
        );
        System.out.println(thrown.getMessage());
        assertTrue(thrown.getMessage().equals("Вещь с id  = [77] не существует"));
    }

    @Test
    void getItemWithBookings() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);
        bookingNumberTwo.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberTwo.setEnd(LocalDateTime.of(2024, 05, 29, 23, 33, 33));

        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberThree.getId(), true);

        ItemDtoWithBooking bookingSaved = itemService.getItemWithBooking(owner.getId(), itemDto.getId());

        assertNotNull(bookingSaved.getLastBooking());
        assertNotNull(bookingSaved.getNextBooking());
    }

    @Test
    void getItemWithBookingsByBooker() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        bookingNumberTwo = bookingService.createBooking(booker.getId(), bookingNumberTwo);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberTwo.getId(), true);
        bookingNumberTwo.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberTwo.setEnd(LocalDateTime.of(2024, 05, 29, 23, 33, 33));

        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);
        bookingService.confirmOrRejectBooking(owner.getId(), bookingNumberThree.getId(), true);

        ItemDtoWithBooking bookingSaved = itemService.getItemWithBooking(booker.getId(), itemDto.getId());

        assertNull(bookingSaved.getLastBooking());
        assertNull(bookingSaved.getNextBooking());
    }

    @Test
    void getItemWithoutBookings() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);

        ItemDtoWithBooking itemSaved = itemService.getItemWithBooking(owner.getId(), itemDto.getId());

        assertNull(itemSaved.getLastBooking());
        assertNull(itemSaved.getNextBooking());
    }

    @Test
    void addNewComment() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);
        bookingNumberThree.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberThree.setEnd(LocalDateTime.of(2024, 05, 29, 23, 33, 33));

        Comment newComment = itemService.addNewComment(booker.getId(), itemDto.getId(), comment);
        assertEquals(newComment.getText(), comment.getText());
    }

    @Test
    void addNewCommentWithEmptyText() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        ItemDto itemDto = itemService.addNewItemWithoutRequest(owner.getId(), itemWithoutRequest);
        bookingNumberThree = bookingService.createBooking(booker.getId(), bookingNumberThree);
        bookingNumberThree.setStart(LocalDateTime.of(2024, 04, 29, 23, 33, 33));
        bookingNumberThree.setEnd(LocalDateTime.of(2024, 07, 29, 23, 33, 33));

        ItemValidationException thrown = assertThrows(
                ItemValidationException.class,
                () -> itemService.addNewComment(booker.getId(), itemDto.getId(), comment),
                "Бронирование еще не завершено."
        );

        assertTrue(thrown.getMessage().equals("Бронирование еще не завершено."));
    }


}