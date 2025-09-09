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

    @Test
    public void testCreateBookMergesAuthorFields() throws Exception {
        // 1. Save an author with minimal info
        AuthorEntity baseAuthor = AuthorEntity.builder()
                .name("Merge Author")
                .active(true)
                .build();
        AuthorEntity savedAuthor = bookService.save(
                "111-MERGE-TEST",
                BookEntity.builder()
                        .isbn("111-MERGE-TEST")
                        .title("Temp")
                        .author(baseAuthor)
                        .price(BigDecimal.ONE)
                        .pages(10)
                        .published(true)
                        .build()
        ).getAuthor();

        // 2. Now create a new book referencing the same author ID but with extra info
        AuthorEntity newAuthorData = AuthorEntity.builder()
                .id(savedAuthor.getId())       // same ID
                .age(45)
                .rating(4.7)
                .totalBooks(12)
                .build();

        BookEntity newBook = BookEntity.builder()
                .isbn("222-MERGE-TEST")
                .title("Book With Merge")
                .author(newAuthorData)
                .price(BigDecimal.TEN)
                .pages(200)
                .published(true)
                .build();

        String json = objectMapper.writeValueAsString(newBook);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/" + newBook.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value("Merge Author")) // kept from old
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.age").value(45))              // new field added
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.rating").value(4.7));         // new field added
    }

    @Test
    public void testPartialUpdateMergesAuthorFields() throws Exception {
        BookEntity book = bookService.save(
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull()).getIsbn(),
                TestDataUtils.createBookFull(TestDataUtils.createAuthorFull())
        );

        // Author already has a name and active
        AuthorEntity updatedAuthor = AuthorEntity.builder()
                .id(book.getAuthor().getId())
                .totalBooks(50)   // new field only
                .build();

        book.setAuthor(updatedAuthor);
        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value("Arther")) // unchanged
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.totalBooks").value(50)); // merged
    }

    @Test
    public void testCreateBookWithNewAuthorWithoutId() throws Exception {
        AuthorEntity newAuthor = AuthorEntity.builder()
                .name("Fresh Author")
                .age(30)
                .active(true)
                .genres(List.of("Horror", "Drama"))
                .build();

        BookEntity book = BookEntity.builder()
                .isbn("333-NEW-AUTHOR")
                .title("Brand New Book")
                .author(newAuthor)   // no ID here
                .price(BigDecimal.valueOf(25))
                .pages(300)
                .published(false)
                .tags(List.of("Thriller"))
                .ratings(List.of(4.0, 4.5))
                .build();

        String json = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value("Fresh Author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.genres[0]").value("Horror"));
    }
}
