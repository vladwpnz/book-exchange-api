package com.friends.sharing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FriendsSharingIT {

    @Autowired
    MockMvc mockMvc;

    final RequestPostProcessor postProcessor = SecurityMockMvcRequestPostProcessors
            .httpBasic("ignat@gmail.com", "1234");

    final String createUser = "INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vlad', 'ignat@gmail.com', '$2a$10$Hzdg8upvCxY8wqZAyq79Ou1szV6sS6Xy55GmDyOqgz8ZKbMsklZ1C', 0)";
    final String createAdmin = "INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vlad', 'ignat@gmail.com', '$2a$10$Hzdg8upvCxY8wqZAyq79Ou1szV6sS6Xy55GmDyOqgz8ZKbMsklZ1C', 1)";

    @Test
    @DisplayName("Test for POST /register endpoint")
    void testRegisterEndpoint() throws Exception {
        var requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name":"vlad",
                        "email":"ignat@gmail.com",
                        "password":"center",
                        "authority":"user"
                        }""");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().bytes("Successfully registered, your email is your username".getBytes()));
    }

    @Test
    @DisplayName("Test for POST /register endpoint(wrong role provided)")
    void testRegisterEndpoint_WrongRole() throws Exception {
        var requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name":"vlad",
                        "email":"ignat@gmail.com",
                        "password":"center",
                        "authority":"godUser"
                        }""");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().bytes("Wrong authority provided".getBytes()));
    }

    @Test
    @DisplayName("Test for POST /register endpoint(such a user already registered)")
    @Sql(statements = createUser)
    void testRegisterEndpoint_AlreadyRegistered() throws Exception {
        var requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name":"vlad",
                        "email":"ignat@gmail.com",
                        "password":"center",
                        "authority":"user"
                        }""");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().bytes("Such a user already exists!".getBytes()));
    }

    @Test
    @DisplayName("Test for POST /book/add endpoint")
    @Sql(statements = createUser)
    void testAddBook() throws Exception {
        var requestBuilder = post("/book/add")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"author\":\"Joshua Bloch\",\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.person.name").value("vlad"))
                .andExpect(jsonPath("$.person.email").value("ignat@gmail.com"));
    }

    @Test
    @DisplayName("Test for GET /held endpoint")
    @Sql(statements = {createUser,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 2, 1, 'Joshua Bloch', 'Effective Java'), (2, 1, 1, 'Hahaha', 'Python')"
    })
    void testGetHeldBooks() throws Exception {
        var requestBuilder = get("/held").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.books[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.books[0].person.name").value("vadim"))
                .andExpect(jsonPath("$.books[0].person.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.books[1].author").value("Hahaha"))
                .andExpect(jsonPath("$.books[1].title").value("Python"))
                .andExpect(jsonPath("$.books[1].person.name").value("vlad"))
                .andExpect(jsonPath("$.books[1].person.email").value("ignat@gmail.com"));
    }

    @Test
    @DisplayName("Test for GET /owned endpoint")
    @Sql(statements = {createUser,
            "INSERT INTO books(book_id, owner_id, holder_id, author, title) " +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testGetOwnedBooks() throws Exception {
        var requestBuilder = get("/owned").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.books[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.books[0].person.name").value("vlad"))
                .andExpect(jsonPath("$.books[0].person.email").value("ignat@gmail.com"));
    }

    @Test
    @DisplayName("Test for catalog browse and add flow")
    @Sql(statements = createUser)
    void testCatalogBrowseAndAddFlow() throws Exception {
        mockMvc.perform(get("/catalog/books").with(postProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books").isArray());

        mockMvc.perform(get("/catalog/books")
                        .with(postProcessor)
                        .param("query", "Domain-Driven"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.books[0].author").value("Eric Evans"));

        mockMvc.perform(get("/catalog/books/6").with(postProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.author").value("J.R.R. Tolkien"));

        mockMvc.perform(post("/book/add/from-catalog")
                        .with(postProcessor)
                        .param("id", "6"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.author").value("J.R.R. Tolkien"));

        mockMvc.perform(get("/owned").with(postProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$.books[0].author").value("J.R.R. Tolkien"));
    }

    @Test
    @DisplayName("Test for GET /items endpoint")
    @Sql(statements = {createAdmin,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java'), (2, 2, 1, 'Hahaha', 'Python')"
    })
    void testGetItems() throws Exception {
        var requestBuilder = get("/items").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].book_id").value(1))
                .andExpect(jsonPath("$.books[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.books[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.books[0].holder_id").value(1))
                .andExpect(jsonPath("$.books[0].owner_id").value(1))
                .andExpect(jsonPath("$.books[1].book_id").value(2))
                .andExpect(jsonPath("$.books[1].author").value("Hahaha"))
                .andExpect(jsonPath("$.books[1].title").value("Python"))
                .andExpect(jsonPath("$.books[1].holder_id").value(1))
                .andExpect(jsonPath("$.books[1].owner_id").value(2));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint")
    @Sql(statements = {createUser,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title) " +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testShareBook() throws Exception {
        var requestBuilder = post("/book/share")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.person.name").value("vadim"))
                .andExpect(jsonPath("$.person.email").value("email@gmail.com"));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint(no book)")
    @Sql(statements = createUser)
    void testShareBook_NoBook() throws Exception {
        var requestBuilder = post("/book/share")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You do not own a book with that title!"));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint(already given)")
    @Sql(statements = {createUser,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 2, 'Joshua Bloch', 'Effective Java')"
    })
    void testShareBook_AlreadyGiven() throws Exception {
        var requestBuilder = post("/book/share")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You have already given this book to someone!"));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint(no user)")
    @Sql(statements = {createUser,
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testShareBook_NoUser() throws Exception {
        var requestBuilder = post("/book/share")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("There are no users with that username!!"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint")
    @Sql(statements = {createUser,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title) " +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testGiveBook() throws Exception {
        var requestBuilder = post("/book/give")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.person.name").value("vadim"))
                .andExpect(jsonPath("$.person.email").value("email@gmail.com"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint(no book)")
    @Sql(statements = createUser)
    void testGiveBook_NoBook() throws Exception {
        var requestBuilder = post("/book/give")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You do not own a book with that title!"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint(already given)")
    @Sql(statements = {createUser,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 2, 'Joshua Bloch', 'Effective Java')"
    })
    void testGiveBook_AlreadyGiven() throws Exception {
        var requestBuilder = post("/book/give")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You have already given this book to someone!"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint(no user)")
    @Sql(statements = {createUser,
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testGiveBook_NoUser() throws Exception {
        var requestBuilder = post("/book/give")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"email@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("There are no users with that username!!"));
    }

    @Test
    @DisplayName("Test for POST /book/return endpoint")
    @Sql(statements = {createUser,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 2, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testReturnBook() throws Exception {
        var requestBuilder = post("/book/return")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().bytes("The book was returned".getBytes()));
    }

    @Test
    @DisplayName("Test for POST /book/return endpoint(no book)")
    @Sql(statements = createUser)
    void testReturnBook_NoBook() throws Exception {
        var requestBuilder = post("/book/return")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You do not hold a book with that title!"));
    }

    @Test
    @DisplayName("Test for POST /book/return endpoint(already owner)")
    @Sql(statements = {createUser,
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testReturnBook_AlreadyOwner() throws Exception {
        var requestBuilder = post("/book/return")
                .with(postProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You are the owner of this book!"));
    }

    @Test
    @DisplayName("Test for DELETE /book/delete endpoint")
    @Sql(statements = {createAdmin,
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 1, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testDeleteBook() throws Exception {
        var requestBuilder = delete("/book/delete?id=1").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().bytes("Book deleted".getBytes()));
    }

    @Test
    @DisplayName("Test for DELETE /book/delete endpoint(no book)")
    @Sql(statements = createAdmin)
    void testDeleteBook_NoBook() throws Exception {
        var requestBuilder = delete("/book/delete?id=1").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().bytes("Book not found".getBytes()));
    }

    @Test
    @DisplayName("Test for POST /book/return/force endpoint")
    @Sql(statements = {createAdmin,
            "INSERT INTO user(user_id, email, name, authority) VALUES (2, 'email@gmail.com', 'vadim', 0)",
            "INSERT INTO books(book_id, owner_id, holder_id, author, title)" +
                    "VALUES (1, 2, 1, 'Joshua Bloch', 'Effective Java')"
    })
    void testForceReturnBook() throws Exception {
        var requestBuilder = post("/book/return/force?id=1").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().bytes("The book was returned".getBytes()));
    }

    @Test
    @DisplayName("Test for DELETE /book/return/force endpoint(no book)")
    @Sql(statements = createAdmin)
    void testForceReturnBook_NoBook() throws Exception {
        var requestBuilder = post("/book/return/force?id=1").with(postProcessor);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().bytes("Book not found".getBytes()));
    }
}
