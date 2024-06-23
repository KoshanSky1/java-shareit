package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithoutOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoWithoutOwner;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestDtoWithAnswers;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addNewItemRequest(long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();

        if (itemRequestDto.getDescription() == null) {
            throw new ItemValidationException("Описание не может быть ппустым");
        }

        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(userService.getUser(userId).orElseThrow());
        itemRequest.setCreated(LocalDateTime.now());
        log.info(format("Создан запрос вещи: %s", itemRequest));
        repository.save(itemRequest);

        return repository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithAnswers> findItemRequestsByUserId(long requestorId) {
        userService.getUser(requestorId);

        List<ItemRequestDtoWithAnswers> itemRequestsWithAnswers = new ArrayList<>();
        List<ItemDtoWithoutOwner> items = new ArrayList<>();
        List<ItemRequest> requests = repository.findAllByRequestorId(requestorId);

        Map<ItemRequest, List<Item>> answers = itemRepository.findAllByRequestIn(requests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));

        if (!requests.isEmpty()) {
            for (ItemRequest itemRequest : requests) {
                if (!answers.isEmpty()) {
                    for (Item item : answers.get(itemRequest)) {
                        items.add(toItemDtoWithoutOwner(item));

                    }
                    itemRequestsWithAnswers.add(toItemRequestDtoWithAnswers(itemRequest, items));
                } else {
                    itemRequestsWithAnswers.add(toItemRequestDtoWithAnswers(itemRequest, items));
                }
            }
        }
        itemRequestsWithAnswers.sort(Comparator.comparing(ItemRequestDtoWithAnswers::getCreated));
        log.info(format("Сформирован список апросов вместе с данными об ответах на них для пользователя id= %s",
                requestorId));

        return itemRequestsWithAnswers;
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getAllItemRequests(long requestorId, int from, int size) {
        if (from < 0) {
            throw new ItemValidationException("Индекс первого элемента не может быть отрицательным");
        }

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequestDtoWithAnswers> itemRequestsWithAnswers = new ArrayList<>();
        List<ItemDtoWithoutOwner> items = new ArrayList<>();

        List<ItemRequest> requests = repository.findAll(page)
                .getContent();

        Map<ItemRequest, List<Item>> answers = itemRepository.findAllByRequestIn(requests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));

        if (!requests.isEmpty()) {
            for (ItemRequest itemRequest : requests) {
                if (itemRequest.getRequestor().getId() == requestorId) {
                    return itemRequestsWithAnswers;
                }
                if (!answers.isEmpty()) {
                    for (Item item : answers.get(itemRequest)) {
                        items.add(toItemDtoWithoutOwner(item));
                    }
                    itemRequestsWithAnswers.add(toItemRequestDtoWithAnswers(itemRequest, items));
                } else {
                    itemRequestsWithAnswers.add(toItemRequestDtoWithAnswers(itemRequest, null));
                }
            }
        }

        itemRequestsWithAnswers.sort(Comparator.comparing(ItemRequestDtoWithAnswers::getCreated));
        log.info("Сформирован список апросов вместе с данными об ответах на них ");

        return itemRequestsWithAnswers;
    }

    @Override
    public ItemRequestDtoWithAnswers getItemRequestById(long userId, long requestId) {
        userService.getUser(userId);
        findItemRequestById(requestId);

        ItemRequest itemRequest = repository.findById(requestId).orElseThrow();
        List<Item> answers = itemRepository.findAllByRequestId(requestId);
        List<ItemDtoWithoutOwner> items = new ArrayList<>();

        for (Item item : answers) {
            items.add(toItemDtoWithoutOwner(item));
        }
        log.info(format("Найден запрос вещи с id  = [%s]", requestId));

        return toItemRequestDtoWithAnswers(itemRequest, items);

    }

    @Override
    public Optional<ItemRequest> findItemRequestById(long requestId) {

        if (repository.findById(requestId).isEmpty()) {
            throw new ItemNotFoundException(format("Запрос с id  = [%s] не существует", requestId));
        }

        return repository.findById(requestId);
    }

}