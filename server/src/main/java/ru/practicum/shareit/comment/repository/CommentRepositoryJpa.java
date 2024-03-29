package ru.practicum.shareit.comment.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepositoryJpa  extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem(Item item);

    List<Comment> findAllByItemIn(List<Item> items, Sort created);
}
