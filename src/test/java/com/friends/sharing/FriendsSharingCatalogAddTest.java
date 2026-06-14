package com.friends.sharing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FriendsSharingCatalogAddTest {
    @Autowired
    MockMvc mockMvc;

    final RequestPostProcessor postProcessor = SecurityMockMvcRequestPostProcessors
            .httpBasic("ignat@gmail.com", "1234");
    final RequestPostProcessor secondUserPostProcessor = SecurityMockMvcRequestPostProcessors
            .httpBasic("reader@gmail.com", "1234");

    final String createUser = "INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vlad', 'ignat@gmail.com', '$2a$10$Hzdg8upvCxY8wqZAyq79Ou1szV6sS6Xy55GmDyOqgz8ZKbMsklZ1C', 0)";
    final String createSecondUser = "INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (2, 'reader', 'reader@gmail.com', '$2a$10$Hzdg8upvCxY8wqZAyq79Ou1szV6sS6Xy55GmDyOqgz8ZKbMsklZ1C', 0)";

    @Test
    @DisplayName("Test for first POST /book/add/from-catalog endpoint")
    @Sql(statements = createUser)
    void testAddBookFromCatalog_FirstAddSucceeds() throws Exception {
        mockMvc.perform(post("/book/add/from-catalog")
                        .with(postProcessor)
                        .param("id", "6"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.author").value("J.R.R. Tolkien"))
                .andExpect(jsonPath("$.person.name").value("vlad"))
                .andExpect(jsonPath("$.person.email").value("ignat@gmail.com"));
    }

    @Test
    @DisplayName("Test for duplicate POST /book/add/from-catalog endpoint")
    @Sql(statements = createUser)
    void testAddBookFromCatalog_DuplicateForSameUserReturnsConflict() throws Exception {
        mockMvc.perform(post("/book/add/from-catalog")
                        .with(postProcessor)
                        .param("id", "6"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/book/add/from-catalog")
                        .with(postProcessor)
                        .param("id", "6"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("You have already added this catalog book"));
    }

    @Test
    @DisplayName("Test for different users adding the same catalog book")
    @Sql(statements = {createUser, createSecondUser})
    void testAddBookFromCatalog_DifferentUserCanAddSameCatalogBook() throws Exception {
        mockMvc.perform(post("/book/add/from-catalog")
                        .with(postProcessor)
                        .param("id", "6"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/book/add/from-catalog")
                        .with(secondUserPostProcessor)
                        .param("id", "6"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.author").value("J.R.R. Tolkien"))
                .andExpect(jsonPath("$.person.name").value("reader"))
                .andExpect(jsonPath("$.person.email").value("reader@gmail.com"));
    }

    @Test
    @DisplayName("Test for POST /book/add endpoint after catalog add")
    @Sql(statements = createUser)
    void testManualAddStillWorksAfterCatalogAdd() throws Exception {
        mockMvc.perform(post("/book/add/from-catalog")
                        .with(postProcessor)
                        .param("id", "6"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/book/add")
                        .with(postProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"J.R.R. Tolkien\",\"title\":\"The Hobbit\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.author").value("J.R.R. Tolkien"));
    }
}
