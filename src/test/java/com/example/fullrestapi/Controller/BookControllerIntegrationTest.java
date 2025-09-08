package com.example.fullrestapi.Controller;

import com.example.fullrestapi.Service.BookService;
import com.example.fullrestapi.Utils.TestDataUtils;
import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.domain.entities.BookEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateBook() throws Exception {
        AuthorEntity author = TestDataUtils.createAuthorFull();
        BookEntity book = TestDataUtils.createBookFull(author);
        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(49.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value("Arther"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ratings[1]").value(4.5));
    }

    @Test
    public void testCreateBookWithExistingISBNReturnsConflict() throws Exception {
        AuthorEntity author = TestDataUtils.createAuthorFull();
        BookEntity book = TestDataUtils.createBookFull(author);
        bookService.save(book.getIsbn(), book); // already saved

        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testGetAllBooks() throws Exception {
        BookEntity book = bookService.save(
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull()).getIsbn(),
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull())
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].isbn").value(book.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.active").value(true));
    }

    @Test
    public void testGetBookByIsbn() throws Exception {
        BookEntity book = bookService.save(
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull()).getIsbn(),
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull())
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getIsbn()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value("Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.genres[1]").value("Sci-Fi"));
    }

    @Test
    public void testPartialUpdateBook() throws Exception {
        BookEntity book = bookService.save(
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull()).getIsbn(),
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull())
        );

        book.setPages(400);
        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Java Learn")); // unchanged
    }

    @Test
    public void testUpdateNonExistentBookReturns404() throws Exception {
        AuthorEntity author = TestDataUtils.createAuthorFull();
        BookEntity book = TestDataUtils.createBookFull(author);

        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/9999-XXXX")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testPartialUpdateBookWithEmptyCollections() throws Exception {
        BookEntity book = bookService.save(
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull()).getIsbn(),
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull())
        );

        book.setTags(List.of());
        book.setRatings(List.of());

        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ratings").isEmpty());
    }

    @Test
    public void testCreateBookWithEmptyCollections() throws Exception {
        AuthorEntity author = TestDataUtils.createAuthorFull();
        BookEntity book = BookEntity.builder()
                .isbn("000-EMPTY-BOOK")
                .title("Empty Collections")
                .author(author)
                .tags(List.of())
                .ratings(List.of())
                .price(BigDecimal.TEN)
                .pages(100)
                .published(false)
                .build();

        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ratings").isEmpty());
    }

    @Test
    public void testDeleteBook() throws Exception {
        BookEntity book = bookService.save(
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull()).getIsbn(),
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull())
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + book.getIsbn()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
