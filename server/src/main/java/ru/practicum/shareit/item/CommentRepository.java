package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentDto> findAllByItem_Id(long itemId);

    List<CommentDto> findAllByItemIn(List<Item> items);
}
