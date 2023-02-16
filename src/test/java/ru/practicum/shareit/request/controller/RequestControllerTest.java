package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestAuthor;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    private Item item;
    private static final Long ID = 1L;
    private static final String DESCRIPTION_R = "description";
    private static final User USER = new User(1L, "name", "name@mail.org");
    private static final LocalDateTime CREATED = LocalDateTime.now();
    private static final String NAME = "name";
    private static final RequestDtoInput REQUESTDTOINPUT = new RequestDtoInput(ID,ID,DESCRIPTION_R,CREATED);
    private  List<Item> itemList = new ArrayList<>();
    private static final RequestAuthor AUTHOR = new RequestAuthor(1L, "name");
    private Request testRequest = new Request(ID,DESCRIPTION_R,USER,CREATED,itemList,AUTHOR);
    private static final String URL = "/requests";
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    List<Request> requestList = new ArrayList<>();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    RequestService requestService;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        item = new Item(ID,NAME,DESCRIPTION_R,true,USER,ID);
        itemList.add(item);
        requestList.add(testRequest);
    }

    @Test
    void addRequest() throws Exception {
        Mockito
                .when(requestService.create(anyLong(), any())). thenReturn(testRequest);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(REQUESTDTOINPUT))
                .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUserRequest() throws Exception {
        Mockito
                .when(requestService.getAllUserRequest(anyLong())).thenReturn(requestList);
        mockMvc.perform(get(URL)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Mockito
                .when(requestService.getAll(anyLong(),any(),any())).thenReturn(requestList);

        mockMvc.perform(get(URL + "/all")
                .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());

    }

    @Test
    void getById() throws Exception {
        Mockito
                .when(requestService.getById(anyLong(),anyLong())).thenReturn(testRequest);

        mockMvc.perform(get(URL + "/1")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());

    }
}