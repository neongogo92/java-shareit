package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    private ItemShortDto itemShortDto = new ItemShortDto(1L, "Шина",
            "RunFlat", true, 10L, null);

    @Test
    public void addItem() throws Exception {
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(itemShortDto);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", itemShortDto.getOwnerId())
                        .content(mapper.writeValueAsString(itemShortDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemShortDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemShortDto.getRequestId()), Long.class));
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemShortDto);

        mvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", itemShortDto.getOwnerId())
                        .content(mapper.writeValueAsString(itemShortDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemShortDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemShortDto.getRequestId()), Long.class));
    }

    @Test
    public void getItem() throws Exception {
        ItemDto itemDto = createItemDto();
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", itemDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    public void getUserItems() throws Exception {
        ItemDto itemDto = createItemDto();
        when(itemService.getUserItems(anyLong(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", itemDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDto.getOwnerId()), Long.class));
    }


    @Test
    public void searchItems() throws Exception {
        when(itemService.searchItems(anyLong(), anyString(), any()))
                .thenReturn(List.of(itemShortDto));

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=text")
                        .header("X-Sharer-User-Id", itemShortDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemShortDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemShortDto.getRequestId()), Long.class));
    }

    @SneakyThrows
    @Test
    public void addComment() throws Exception {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"); // 6 знаков после запятой
//        CommentDto commentDto = new CommentDto(11L, "Очень жесткая и шумная((", "Доминик", null);
//        commentDto.setCreated(LocalDateTime.now());
//        when(itemService.addComment(anyLong(), anyLong(), any()))
//                .thenReturn(commentDto);
//
//        mvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
//                        .header("X-Sharer-User-Id", 10L)
//                        .content(mapper.writeValueAsString(commentDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
//                .andExpect(jsonPath("$.text", is(commentDto.getText())))
//                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
//                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(formatter)), String.class));

        long itemId = 1L;
        CommentDto commentToCreate = new CommentDto(11L, "Очень жесткая и шумная((", "Доминик", null);
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentToCreate);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON_UTF8) // Установка кодировки
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(mapper.writeValueAsString(commentToCreate), result);
    }


    private ItemDto createItemDto() {
        return new ItemDto(1L, "Apple Iphone 20 Mega Max Plus Pro", "Звонит", true,
                10L, null);
    }
}
