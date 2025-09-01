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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorControllerIntegrationTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthorService authorService;

    @Autowired
    public AuthorControllerIntegrationTest(MockMvc mockMvc, AuthorService authorService) {
        this.mockMvc = mockMvc;
        objectMapper = new ObjectMapper();
        this.authorService = authorService;

    }

    @Test
    public void verifyCreatingAuthorReturning201HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorEntity.setId(null);
        String authorJson = objectMapper.writeValueAsString(authorEntity);
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.status().isCreated()
                );
    }

    @Test
    public void verifyCreatingAuthorReturningSavedAuthor() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorEntity.setId(null);
        String authorJson = objectMapper.writeValueAsString(authorEntity);
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.id").isNumber()
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.name").value("Arther")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.age").value("80")
                );
    }

    @Test
    public void verifyGettingAllAuthorReturning200HttpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyGettingAllAuthorReturningAllDataRight() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorEntity.setId(null);
        authorService.save(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.content[0].id").isNumber()
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.content[0].name").value("Arther")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.content[0].age").value("80")
                );
    }

    @Test
    public void verifyGettingSpecificAuthorReturning200HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorEntity.setId(null);
        authorService.save(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyGettingSpecificAuthorReturning400HttpStatusIfAuthorNotExist() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorEntity.setId(null);
        authorService.save(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isNotFound()
                );
    }

    @Test
    public void verifyGettingOneAuthorReturningAllDataRight() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorEntity.setId(null);
        authorService.save(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.id").isNumber()
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.name").value("Arther")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.age").value("80")
                );
    }

    @Test
    public void verifyFullUpdatingAuthorReturning200HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);

        authorEntity.setName("new name");
        authorEntity.setAge(50);
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyFullUpdatingAuthorReturning404HttpStatusIfNotExists() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorService.save(authorEntity);

        authorEntity.setName("new name");
        authorEntity.setAge(50);
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.status().isNotFound()
                );
    }

    @Test
    public void verifyFullUpdatingAuthorReturningDataRight() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);

        authorEntity.setName("new name");
        authorEntity.setAge(50);
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.id").value(savedAuthorEntity.getId())
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.name").value("new name")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.age").value("50")
                );
    }

    @Test
    public void verifyPartialUpdatingNameAndAgeAuthorReturning200HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);

        authorEntity.setName("new name");
        authorEntity.setAge(50);
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyPartialUpdatingNameOnlyAuthorReturning200HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);

        authorEntity.setName("new name");
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void verifyPartialUpdatingAuthorReturning404HttpStatusIfNotExists() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        authorService.save(authorEntity);

        authorEntity.setName("new name");
        authorEntity.setAge(50);
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.status().isNotFound()
                );
    }

    @Test
    public void verifyPartialUpdatingAuthorReturningDataRight() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);

        authorEntity.setName("new name");
        authorEntity.setAge(70);
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.id").value(savedAuthorEntity.getId())
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.name").value("new name")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.age").value("70")
                );
    }

    @Test
    public void verifyPartialUpdatingOnlyNameAuthorReturningDataRight() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);

        authorEntity.setName("new name");
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.id").value(savedAuthorEntity.getId())
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.name").value("new name")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.age").value("80")
                );
    }

    @Test
    public void verifyDeleteAuthorReturning200HttpStatus() throws Exception {
        AuthorEntity authorEntity = TestDataUtils.createAuthorA();
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);
        authorService.delete(savedAuthorEntity.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/" + savedAuthorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.status().isNoContent()
                );
    }
}
