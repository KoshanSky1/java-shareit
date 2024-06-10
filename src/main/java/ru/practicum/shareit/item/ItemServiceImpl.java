package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingReducedDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoWithBooking;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item addNewItem(long userId, Item item) {
        if (item.getName().isBlank() || item.getDescription() == null || item.getAvailable() == null) {
            throw new ItemValidationException("Не заполнено одно из обязательных полей: имя, описание или статус");
        }
        item.setOwner(userService.getUser(userId).orElseThrow());
        item.setAvailable(true);
        log.info(format("Создан предмет: %s", item));
        repository.save(item);
        return repository.save(item);
    }

    @Override
    public Item editItem(long userId, long itemId, Item item) {
        item.setOwner(userService.getUser(userId).orElseThrow());
        Item itemUpdated = getItem(itemId).orElseThrow();

        if (!itemUpdated.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователи не совпадают");
        }

        itemUpdated.setName(getStringValueOrDefault(item.getName(), itemUpdated.getName()));
        itemUpdated.setDescription(getStringValueOrDefault(item.getDescription(), itemUpdated.getDescription()));
        itemUpdated.setAvailable(getBooleanValueOrDefault(item.getAvailable(), itemUpdated.getAvailable()));
        log.info(format("Обновлён предмет: %s", itemId));

        return repository.save(itemUpdated);
    }

    @Override
    public Optional<Item> getItem(long itemId) {
        if (repository.findById(itemId).isEmpty()) {
            throw new ItemNotFoundException(format("Вещь с id  = [%s] не существует", itemId));
        }

        return repository.findById(itemId);
    }

    @Override
    public List<ItemDtoWithBooking> getItems(long ownerId) {
        List<ItemDtoWithBooking> itemsWithBookings = new ArrayList<>();
        List<Item> items = repository.findAllByOwnerId(ownerId);

        for (Item item : items) {
            Booking last = getLastBooking(item.getId());
            Booking next = getNextBooking(item.getId());
            List<CommentDto> comments = getComments(item.getId());

            if (last != null && next != null) {
                itemsWithBookings.add(toItemDtoWithBooking(item, toBookingReducedDto(last), toBookingReducedDto(next),
                        comments));
            } else if (last != null) {
                itemsWithBookings.add(toItemDtoWithBooking(item, toBookingReducedDto(last), null, comments));
            } else if (next != null) {
                itemsWithBookings.add(toItemDtoWithBooking(item, null, toBookingReducedDto(next), comments));
            } else {
                itemsWithBookings.add(toItemDtoWithBooking(item, null, null, comments));
            }
        }
        itemsWithBookings.sort(Comparator.comparing(ItemDtoWithBooking::getId));
        log.info(format("Сформирован список предметов для пользователя id= %s", ownerId));

        return itemsWithBookings;
    }

    @Override
    public ItemDtoWithBooking getItemWithBooking(long userId, long itemId) {
        Item item = getItem(itemId).orElseThrow();
        Booking last = null;
        Booking next = null;
        List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId());
        log.info(format("Сформирован список предметов для пользователя  id= %s", userId));

        if (userId == item.getOwner().getId()) {
            last = getLastBooking(itemId);
            next = getNextBooking(itemId);

            if (last != null && next != null) {
                return toItemDtoWithBooking(item, toBookingReducedDto(last), toBookingReducedDto(next), comments);
            } else if (last != null) {
                return toItemDtoWithBooking(item, toBookingReducedDto(last), null, comments);
            } else if (next != null) {
                return toItemDtoWithBooking(item, null, toBookingReducedDto(next), comments);
            } else {
                return toItemDtoWithBooking(item, null, null, comments);
            }
        } else {
            return toItemDtoWithBooking(item, null, null, comments);
        }

    }

    @Override
    public List<Item> searchItems(long userId, String text) {
        List<Item> foundItems = new ArrayList<>();
        if (text.isEmpty()) {
            return foundItems;
        }
        log.info(format("Сформирован список предметов, содержащих текст %s", text));
        return repository.search(text);
    }

    @Override
    public Comment addNewComment(long userId, long itemId, Comment comment) {
        Booking bookingForAComment = null;
        List<Booking> bookings = bookingRepository.findByBooker_Id(userId);
        for (Booking booking : bookings) {
            if (booking.getItem().getId() == itemId) {
                bookingForAComment = booking;
            }
        }
        if (bookingForAComment == null || comment.getText().isEmpty()) {
            throw new ItemValidationException("Комментарий не может быть пустым.");
        }
        if (bookingForAComment.getEnd().isAfter(LocalDateTime.now())) {
            throw new ItemValidationException("Бронирование еще не завершено.");
        }
        log.info(format("Создан коментарий: %s", comment));
        comment.setItem(getItem(itemId).orElseThrow());
        comment.setAuthor(userService.getUser(userId).orElseThrow());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return comment;

    }

    private static String getStringValueOrDefault(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private static boolean getBooleanValueOrDefault(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    private Booking getLastBooking(long itemId) {
        return bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
    }

    private Booking getNextBooking(long itemId) {
        return bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
    }

    private List<CommentDto> getComments(long itemId) {
        return commentRepository.findAllByItem_Id(itemId);
    }

}