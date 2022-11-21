package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentRepositoryJpa  extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemContaining(Long item);
}
