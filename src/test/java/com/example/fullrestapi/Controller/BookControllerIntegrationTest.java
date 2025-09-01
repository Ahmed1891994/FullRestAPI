package com.example.fullrestapi.Controller;

import com.example.fullrestapi.Service.BookService;
import com.example.fullrestapi.Utils.TestDataUtils;
import com.example.fullrestapi.domain.entities.AuthorEntity;
import com.example.fullrestapi.domain.entities.BookEntity;
import com.example.fullrestapi.mappers.Impl.BookMapperImpl;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BookService bookService;
    @Autowired
    private BookMapperImpl bookMapperImpl;

    @Autowired
    public BookControllerIntegrationTest(MockMvc mockMvc,BookService bookService) {
        this.mockMvc = mockMvc;
        objectMapper = new ObjectMapper();
        this.bookService = bookService;
    }

    @Test
    public void verifyCreatingBookReturning201HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        BookEntity bookSavedEntity = TestDataUtils.createBookA(authorEntity);
        String bookJson = objectMapper.writeValueAsString(bookSavedEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/"+bookSavedEntity.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(
                        MockMvcResultMatchers.status().isCreated()
                );
    }

    @Test
    public void verifyCreatingBookReturningCreatedBook() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        BookEntity bookSavedEntity = TestDataUtils.createBookA(authorEntity);
        String bookJson = objectMapper.writeValueAsString(bookSavedEntity);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/books/"+bookSavedEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.isbn").value(bookSavedEntity.getIsbn())
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.title").value(bookSavedEntity.getTitle())
                );
    }

    @Test
    public void verifyGettingAllBooksReturning200Status() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyGettingAllBooksReturningAllBooks() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.content[0].isbn").value(savedBookEntity.getIsbn())
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.content[0].title").value(savedBookEntity.getTitle())
                );
    }

    @Test
    public void verifyGettingOneBooksReturning200Status() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        bookService.save("938-13123-123",bookEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/books/938-13123-123")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyGettingOneBooksReturning404StatusIfBookNotExists() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        bookService.save("938-13123-123",bookEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/books/938-13123-12323")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isNotFound()
                );
    }

    @Test
    public void verifyGettingOneBookReturningAllDataRight() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/books/"+bookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.isbn").value(savedBookEntity.getIsbn())
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.title").value(savedBookEntity.getTitle())
                );
    }

    @Test
    public void verifyFullUpdateBooksReturning200Status() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        bookEntity.setTitle("new title");
        String bookJson = objectMapper.writeValueAsString(bookEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/books/"+savedBookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyFullUpdateBooksReturningRightData() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        bookEntity.setTitle("new title");
        String bookJson = objectMapper.writeValueAsString(bookEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/books/"+savedBookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.title").value("new title")
                );
    }

    @Test
    public void verifyPartialUpdateBooksReturning200Status() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        bookEntity.setTitle("new title");
        String bookJson = objectMapper.writeValueAsString(bookEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/books/"+savedBookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyPartialUpdateBooksReturningRightData() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        bookEntity.setTitle("new title");
        String bookJson = objectMapper.writeValueAsString(bookEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/books/"+savedBookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.title").value("new title")
                );
    }

    @Test
    public void verifyDeleteBooksReturningNoContentStatus() throws Exception {
        BookEntity bookEntity = TestDataUtils.createBookA(null);
        BookEntity savedBookEntity = bookService.save(bookEntity.getIsbn(),bookEntity);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/books/"+savedBookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isNoContent()
                );
    }
}