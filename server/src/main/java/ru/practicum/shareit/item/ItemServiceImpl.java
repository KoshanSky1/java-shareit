package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingReducedDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoWithBooking;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto addNewItemWithoutRequest(long userId, Item item) {
        item.setOwner(userService.getUser(userId).orElseThrow());
        item.setRequest(null);
        item.setAvailable(true);
        log.info(format("Создан предмет: %s", item));
        repository.save(item);
        return toItemDto(item);
    }

    @Override
    public ItemDto addNewItemWithRequest(long userId, Item item, long requestId) {
        item.setOwner(userService.getUser(userId).orElseThrow());
        item.setRequest(requestRepository.findById(requestId).orElseThrow());
        item.setAvailable(true);
        log.info(format("Создан предмет: %s", item));
        repository.save(item);
        return toItemDto(item);
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
    public List<ItemDtoWithBooking> getItems(long ownerId, int from, int size) {

        List<ItemDtoWithBooking> itemsWithBookings = new ArrayList<>();

        List<Item> items = repository.findAllByOwnerId(ownerId);

        Map<Item, List<CommentDto>> comments = commentRepository.findAllByItemIn(items)
                .stream()
                .collect(groupingBy(CommentDto::getItem, toList()));

        Map<Item, List<Booking>> bookings = bookingRepository.findAllByItemIn(items)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        for (Item item : items) {
            Booking last = getLastBooking(bookings.get(item));
            Booking next = getNextBooking(bookings.get(item));

            if (last != null && next != null) {
                itemsWithBookings.add(toItemDtoWithBooking(item, toBookingReducedDto(last), toBookingReducedDto(next),
                        comments.get(item)));
            } else if (last != null) {
                itemsWithBookings.add(toItemDtoWithBooking(item, toBookingReducedDto(last), null,
                        comments.get(item)));
            } else if (next != null) {
                itemsWithBookings.add(toItemDtoWithBooking(item, null, toBookingReducedDto(next),
                        comments.get(item)));
            } else {
                itemsWithBookings.add(toItemDtoWithBooking(item, null, null,
                        comments.get(item)));
            }
        }

        itemsWithBookings.sort(Comparator.comparing(ItemDtoWithBooking::getId));
        log.info(format("Сформирован список предметов для пользователя id= %s", ownerId));

        return pagedResponse(itemsWithBookings, from, size);
    }

    @Override
    public ItemDtoWithBooking getItemWithBooking(long userId, long itemId) {
        Item item = getItem(itemId).orElseThrow();
        Booking last = null;
        Booking next = null;
        List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId());
        log.info(format("Сформирован список предметов для пользователя  id= %s", userId));

        if (userId == item.getOwner().getId()) {
            last = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(item.getId(),
                    LocalDateTime.now(), APPROVED);
            next = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                    LocalDateTime.now(), APPROVED);

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
    public List<Item> searchItems(long userId, String text, int from, int size) {

        List<Item> foundItems = new ArrayList<>();
        if (text.isEmpty()) {
            return foundItems;
        }
        foundItems = repository.search(text);
        log.info(format("Сформирован список предметов, содержащих текст %s", text));
        return itemPagedResponse(foundItems, from, size);
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

    private Booking getLastBooking(List<Booking> bookings) {
        Booking last = null;

        if (bookings != null) {
            bookings
                    .stream()
                    .filter(b -> b.getStatus() == APPROVED)
                    .sorted(Comparator.comparing(Booking::getStart));

            for (Booking booking : bookings) {
                if (booking.getStart().isBefore(LocalDateTime.now())) {
                    last = booking;
                }
            }
        }
        return last;
    }

    private Booking getNextBooking(List<Booking> bookings) {
        Booking next = null;

        if (bookings != null) {
            bookings
                    .stream()
                    .filter(b -> b.getStatus() == APPROVED)
                    .sorted(Comparator.comparing(Booking::getStart));

            for (Booking booking : bookings) {
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    next = booking;
                }
            }
        }
        return next;
    }

    private List<ItemDtoWithBooking> pagedResponse(List<ItemDtoWithBooking> items, int from, int size) {
        int totalBookings = items.size();
        int toIndex = from + size;

        if (from <= totalBookings) {
            if (toIndex > totalBookings) {
                toIndex = totalBookings;
            }
            return items.subList(from, toIndex);
        } else {
            return Collections.emptyList();
        }
    }

    private List<Item> itemPagedResponse(List<Item> items, int from, int size) {
        int totalBookings = items.size();
        int toIndex = from + size;

        if (from <= totalBookings) {
            if (toIndex > totalBookings) {
                toIndex = totalBookings;
            }
            return items.subList(from, toIndex);
        } else {
            return Collections.emptyList();
        }
    }

}