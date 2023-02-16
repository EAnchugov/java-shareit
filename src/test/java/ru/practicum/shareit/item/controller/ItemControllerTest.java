package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.LastBooking;
import ru.practicum.shareit.item.itemDto.NextBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final Long ITEM_ID = 1L;
    private static final Long ID = 1L;
    ItemDto itemDto;
    private static final String ITEM_NAME = "test item name";
    private static final String ITEM_DESCRIPTION = "test item description";
    private static final LastBooking LASTBOOKING = new LastBooking(1L, 1L);
    private static final NextBooking NEXTBOOKING = new NextBooking(2L, 2L);
    private static final ItemDto.Owner OWNER = new ItemDto.Owner(1L, "ownerNAme");
    private static final Long REQUEST = 1L;
    private static final String COMMENT_TEXT = "comment text";
    private static final String AUTHOR_NAME = "author name";
    private static final LocalDateTime NOW = LocalDateTime.of(2022,12,12,12,12,12);
    private static final CommentDto COMMENT_DTO = CommentDto.builder()
            .id(ID)
            .text(COMMENT_TEXT)
            .authorName(AUTHOR_NAME)
            .created(NOW)
            .build();
    List<CommentDto> comments = new ArrayList<>();
    List<ItemDto> itemDtoList = new ArrayList<>();


    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserController userController;
    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mockMvc;
    private UserDto userDto;
    private String search = "search";


    @BeforeEach
    void load() {
         itemDto = ItemDto
                .builder()
                .id(ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .lastBooking(LASTBOOKING)
                .nextBooking(NEXTBOOKING)
                .owner(OWNER)
                .available(true)
                .requestId(REQUEST)
                .comments(comments)
                .build();
    }

    @Test
    void create() throws Exception {
        Mockito
                .when(itemService.create(itemDto, OWNER.getId()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void update() throws Exception {
        itemDto.setName("item 2");
        Mockito
                .when(itemService.update(itemDto, OWNER.getId(),itemDto.getId()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.requestId", is(itemDto)))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void createComment() throws Exception {
        Mockito
                .when(itemService.createComment(ITEM_ID,ID,COMMENT_DTO))
                .thenReturn(COMMENT_DTO);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(COMMENT_DTO))
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(COMMENT_DTO.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(COMMENT_DTO.getText())))
                .andExpect(jsonPath("$.authorName", is(COMMENT_DTO.getAuthorName())))
                .andExpect(jsonPath("$.created", is(COMMENT_DTO.getCreated().toString())));
    }

    @Test
    void testCreate() throws Exception {
        Mockito
                .when(itemService.getAllByOwnerId(anyLong())).thenReturn(itemDtoList);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAll() throws Exception {
        Mockito
                .when(itemService.getAllByOwnerId(anyLong())).thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testSearch() throws Exception {
        itemDtoList.add(itemDto);
        Mockito.when(itemService.search(any())).thenReturn(itemDtoList);
        mockMvc.perform(get("/items/search?text=search")
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateComment() throws Exception {

        Mockito
                .when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(COMMENT_DTO);
        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(COMMENT_DTO))
                        .header("X-Sharer-User-Id", OWNER.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}