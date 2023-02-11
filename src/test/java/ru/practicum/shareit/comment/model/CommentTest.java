package ru.practicum.shareit.comment.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//потрясающий в своей полезности тест
class CommentTest {
    Comment comment = new Comment();

    @Test
    void getId() {
        assertEquals(comment.getId(), null);
    }

    @Test
    void getText() {
        assertEquals(comment.getText(), null);
    }

    @Test
    void getItem() {
        assertEquals(comment.getItem(), null);
    }

    @Test
    void getAuthor() {
        assertEquals(comment.getAuthor(), null);
    }

    @Test
    void getCreated() {
        assertEquals(comment.getCreated(), null);
    }

    @Test
    void setText() {
        comment.setText("sdfsdf");
    }

    @Test
    void builder() {
        Comment.builder().build();
    }
}