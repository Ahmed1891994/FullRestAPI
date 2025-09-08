package com.example.fullrestapi.Controller;

import com.example.fullrestapi.Service.AuthorService;
import com.example.fullrestapi.Utils.TestDataUtils;
import com.example.fullrestapi.domain.entities.AuthorEntity;
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
import java.math.BigInteger;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorService authorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateAuthor() throws Exception {
        AuthorEntity author = TestDataUtils.createAuthorFull();
        author.setId(null); // let DB generate ID
        String json = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(author.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(author.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(author.getActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(author.getRating()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalBooks").value(author.getTotalBooks()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres[0]").value("Fantasy"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scores[1]").value(90))
                .andExpect(MockMvcResultMatchers.jsonPath("$.wealth").value(1000000.50))
                .andExpect(MockMvcResultMatchers.jsonPath("$.followers").value(50000));
    }

    @Test
    public void testGetAllAuthors() throws Exception {
        AuthorEntity author = authorService.save(TestDataUtils.createAuthorFull());

        mockMvc.perform(MockMvcRequestBuilders.get("/authors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(author.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].active").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].wealth").value(1000000.50));
    }

    @Test
    public void testGetAuthorById() throws Exception {
        AuthorEntity author = authorService.save(TestDataUtils.createAuthorFull());

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/" + author.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(author.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres[1]").value("Sci-Fi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.followers").value(50000));
    }

    @Test
    public void testFullUpdateAuthor() throws Exception {
        AuthorEntity author = authorService.save(TestDataUtils.createAuthorFull());
        author.setName("Updated Name");
        author.setAge(70);
        String json = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(70));
    }

    @Test
    public void testPartialUpdateAuthor() throws Exception {
        AuthorEntity author = authorService.save(TestDataUtils.createAuthorFull());
        author.setRating(4.9);
        String json = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/" + author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(4.9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Arther")); // unchanged
    }

    @Test
    public void testDeleteAuthor() throws Exception {
        AuthorEntity author = authorService.save(TestDataUtils.createAuthorFull());

        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/" + author.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUpdateNonExistentAuthorReturns404() throws Exception {
        AuthorEntity author = TestDataUtils.createAuthorFull();
        author.setId(null);
        String json = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testPartialUpdateNonExistentAuthorReturns404() throws Exception {
        AuthorEntity author = new AuthorEntity();
        author.setName("Ghost Author");
        String json = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateAuthorWithEmptyCollections() throws Exception {
        AuthorEntity author = AuthorEntity.builder()
                .name("Empty Lists")
                .age(30)
                .genres(List.of())
                .scores(List.of())
                .followers(BigInteger.ZERO)
                .wealth(BigDecimal.ZERO)
                .build();

        String json = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scores").isEmpty());
    }

}
