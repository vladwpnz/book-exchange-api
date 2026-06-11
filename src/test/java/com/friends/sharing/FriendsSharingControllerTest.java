package com.friends.sharing;

import com.friends.sharing.configuration.security.Authorities;
import com.friends.sharing.configuration.security.SecurityConfig;
import com.friends.sharing.configuration.security.UserAdapter;
import com.friends.sharing.controller.FriendsSharingController;
import com.friends.sharing.dto.request.AddBookRequest;
import com.friends.sharing.dto.request.GiveBookRequest;
import com.friends.sharing.dto.request.RegistrationRequest;
import com.friends.sharing.dto.request.ReturnBookRequest;
import com.friends.sharing.dto.request.UpdateProfileRequest;
import com.friends.sharing.dto.response.*;
import com.friends.sharing.exception.ItemException;
import com.friends.sharing.model.User;
import com.friends.sharing.service.FriendsSharingService;
import com.friends.sharing.service.UserDetailsServiceImp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(FriendsSharingController.class)
@Import(SecurityConfig.class)
public class FriendsSharingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FriendsSharingService friendsSharingService;
    @MockBean
    UserDetailsServiceImp userDetailsService;

    User user = User.builder().user_id(1L).name("vlad").email("ignat@gmail.com")
            .password("center").authority(Authorities.USER).build();

    @Test
    @DisplayName("Test for POST /register endpoint")
    void testRegisterEndpoint() throws Exception {
        var registrationRequest = new RegistrationRequest(
                "vlad",
                "ignat@gmail.com",
                "center",
                "user"
        );
        var responseEntity = new ResponseEntity<>("Successfully registered, your email is your username", HttpStatus.CREATED);

        when(userDetailsService.register(registrationRequest))
                .thenReturn(responseEntity);

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
        var registrationRequest = new RegistrationRequest(
                "vlad",
                "ignat@gmail.com",
                "center",
                "godUser"
        );
        var responseEntity = new ResponseEntity<>("Wrong authority provided",
                HttpStatus.BAD_REQUEST);

        when(userDetailsService.register(registrationRequest))
                .thenReturn(responseEntity);

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
    void testRegisterEndpoint_AlreadyRegistered() throws Exception {
        var registrationRequest = new RegistrationRequest(
                "vlad",
                "ignat@gmail.com",
                "center",
                "user"
        );
        var responseEntity = new ResponseEntity<>("Such a user already exists!",
                HttpStatus.BAD_REQUEST);

        when(userDetailsService.register(registrationRequest))
                .thenReturn(responseEntity);

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
    @DisplayName("Test for GET /me endpoint")
    void testGetProfile() throws Exception {
        var response = UserDTO.builder()
                .name("vlad")
                .email("ignat@gmail.com")
                .authority(Authorities.USER)
                .build();

        when(friendsSharingService.getProfile(user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = get("/me");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("vlad"))
                .andExpect(jsonPath("$.email").value("ignat@gmail.com"))
                .andExpect(jsonPath("$.authority").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Test for PATCH /me endpoint")
    void testUpdateProfile() throws Exception {
        var request = new UpdateProfileRequest("New Name");
        var response = UserDTO.builder()
                .name("New Name")
                .email("ignat@gmail.com")
                .authority(Authorities.USER)
                .build();

        when(friendsSharingService.updateProfile(request, user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = patch("/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Name\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("ignat@gmail.com"))
                .andExpect(jsonPath("$.authority").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Test for POST /book/add endpoint")
    void testAddBook() throws Exception {
        var request = new AddBookRequest("Joshua Bloch", "Effective Java");
        var response = BookWithUserDTO.builder()
                .author("Joshua Bloch")
                .title("Effective Java")
                .person(UserDTO.builder().name("vlad").email("ignat@gmail.com").build())
                .build();

        when(friendsSharingService.addBook(request, user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/add")
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
    void testGetHeldBooks() throws Exception {
        var expectOne = BookWithUserDTO.builder()
                .author("Joshua Bloch")
                .title("Effective Java")
                .person(UserDTO.builder()
                        .name("vadim")
                        .email("email@gmail.com")
                        .build())
                .build();
        var expectTwo = BookWithUserDTO.builder()
                .author("Hahaha")
                .title("Python")
                .person(UserDTO.builder()
                        .name("vlad")
                        .email("ignat@gmail.com")
                        .build())
                .build();
        var response = ItemsWithUser.builder().books(List.of(expectOne, expectTwo)).build();

        when(friendsSharingService.getHeldItems(user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = get("/held");
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
    void testGetOwnedBooks() throws Exception {
        var expectOne = BookWithUserDTO.builder()
                .author("Joshua Bloch")
                .title("Effective Java")
                .person(UserDTO.builder()
                        .name("vadim")
                        .email("email@gmail.com")
                        .build())
                .build();
        var response = ItemsWithUser.builder().books(List.of(expectOne)).build();

        when(friendsSharingService.getOwnedItems(user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = get("/owned");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.books[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.books[0].person.name").value("vadim"))
                .andExpect(jsonPath("$.books[0].person.email").value("email@gmail.com"));
    }

    @Test
    @DisplayName("Test for OPTIONS /owned CORS preflight")
    void testOwnedCorsPreflight() throws Exception {
        var requestBuilder = options("/owned")
                .header("Origin", "http://localhost:5174")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization, Content-Type, Accept");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5174"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "ADMIN")
    @DisplayName("Test for GET /items endpoint")
    void testGetItems() throws Exception {
        var expectOne = BookDTO.builder()
                .book_id(1L)
                .author("Joshua Bloch")
                .title("Effective Java")
                .holder_id(1L)
                .owner_id(1L)
                .build();
        var expectTwo = BookDTO.builder()
                .book_id(2L)
                .author("Someone")
                .title("Not Effective Java")
                .holder_id(1L)
                .owner_id(2L)
                .build();
        var response = Items.builder().books(List.of(expectOne, expectTwo)).build();

        when(friendsSharingService.getItems()).thenReturn(response);

        var requestBuilder = get("/items");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].book_id").value(1))
                .andExpect(jsonPath("$.books[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.books[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.books[0].holder_id").value(1))
                .andExpect(jsonPath("$.books[0].owner_id").value(1))
                .andExpect(jsonPath("$.books[1].book_id").value(2))
                .andExpect(jsonPath("$.books[1].author").value("Someone"))
                .andExpect(jsonPath("$.books[1].title").value("Not Effective Java"))
                .andExpect(jsonPath("$.books[1].holder_id").value(1))
                .andExpect(jsonPath("$.books[1].owner_id").value(2));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint")
    void testShareBook() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");
        var response = BookWithUserDTO.builder()
                .author("Joshua Bloch")
                .title("Effective Java")
                .person(UserDTO.builder().name("vlad").email("ignat@gmail.com").build())
                .build();

        when(friendsSharingService.shareBook(request, user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.person.name").value("vlad"))
                .andExpect(jsonPath("$.person.email").value("ignat@gmail.com"));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint(no book)")
    void testShareBook_NoBook() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");

        when(friendsSharingService.shareBook(request, user))
                .thenThrow(new ItemException("You do not own a book with that title!"));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You do not own a book with that title!"));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint(already given)")
    void testShareBook_AlreadyGiven() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");

        when(friendsSharingService.shareBook(request, user))
                .thenThrow(new ItemException("You have already given this book to someone!"));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You have already given this book to someone!"));
    }

    @Test
    @DisplayName("Test for POST /book/share endpoint(no user)")
    void testShareBook_NoUser() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");

        when(friendsSharingService.shareBook(request, user))
                .thenThrow(new ItemException("There are no users with that username!!"));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("There are no users with that username!!"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint")
    void testGiveBook() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");
        var response = BookWithUserDTO.builder()
                .author("Joshua Bloch")
                .title("Effective Java")
                .person(UserDTO.builder().name("vlad").email("ignat@gmail.com").build())
                .build();

        when(friendsSharingService.giveBook(request, user)).thenReturn(response);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/give")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.person.name").value("vlad"))
                .andExpect(jsonPath("$.person.email").value("ignat@gmail.com"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint(no book)")
    void testGiveBook_NoBook() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");

        when(friendsSharingService.giveBook(request, user))
                .thenThrow(new ItemException("You do not own a book with that title!"));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/give")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You do not own a book with that title!"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint(already given)")
    void testGiveBook_AlreadyGiven() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");

        when(friendsSharingService.giveBook(request, user))
                .thenThrow(new ItemException("You have already given this book to someone!"));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/give")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You have already given this book to someone!"));
    }

    @Test
    @DisplayName("Test for POST /book/give endpoint(no user)")
    void testGiveBook_NoUser() throws Exception {
        var request = new GiveBookRequest("Effective Java", "ignat@gmail.com");

        when(friendsSharingService.giveBook(request, user))
                .thenThrow(new ItemException("There are no users with that username!!"));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/give")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"ignat@gmail.com\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("There are no users with that username!!"));
    }

    @Test
    @DisplayName("Test for POST /book/return endpoint")
    void testReturnBook() throws Exception {
        var request = new ReturnBookRequest("Effective Java");

        doNothing().when(friendsSharingService).returnBook(request, user);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().bytes("The book was returned".getBytes()));
    }

    @Test
    @DisplayName("Test for POST /book/return endpoint(no book)")
    void testReturnBook_NoBook() throws Exception {
        var request = new ReturnBookRequest("Effective Java");

        doThrow(new ItemException("You do not hold a book with that title!"))
                .when(friendsSharingService).returnBook(request, user);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You do not hold a book with that title!"));
    }

    @Test
    @DisplayName("Test for POST /book/return endpoint(already owner)")
    void testReturnBook_AlreadyOwner() throws Exception {
        var request = new ReturnBookRequest("Effective Java");

        doThrow(new ItemException("You are the owner of this book!"))
                .when(friendsSharingService).returnBook(request, user);
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(
                new UserAdapter(user), null, List.of(new SimpleGrantedAuthority(user.getAuthority().toString()))
        )); //initialize user in program

        var requestBuilder = post("/book/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\"}");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You are the owner of this book!"));
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "ADMIN")
    @DisplayName("Test for DELETE /book/delete endpoint")
    void testDeleteBook() throws Exception {
        when(friendsSharingService.deleteBook(1L)).thenReturn(true);

        var requestBuilder = delete("/book/delete?id=1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().bytes("Book deleted".getBytes()));
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "ADMIN")
    @DisplayName("Test for DELETE /book/delete endpoint(no book)")
    void testDeleteBook_NoBook() throws Exception {
        when(friendsSharingService.deleteBook(1L)).thenReturn(false);

        var requestBuilder = delete("/book/delete?id=1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().bytes("Book not found".getBytes()));
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "ADMIN")
    @DisplayName("Test for POST /book/return/force endpoint")
    void testForceReturnBook() throws Exception {
        when(friendsSharingService.forceReturnBook(1L)).thenReturn(true);

        var requestBuilder = post("/book/return/force?id=1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().bytes("The book was returned".getBytes()));
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "ADMIN")
    @DisplayName("Test for DELETE /book/return/force endpoint(no book)")
    void testForceReturnBook_NoBook() throws Exception {
        when(friendsSharingService.forceReturnBook(1L)).thenReturn(false);

        var requestBuilder = post("/book/return/force?id=1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().bytes("Book not found".getBytes()));
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "USER")
    @DisplayName("Test for POST /book/return/force endpoint(forbidden)")
    void testForceReturnBook_Forbidden() throws Exception {
        var requestBuilder = post("/book/return/force?id=1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test for POST /book/return/force endpoint(unauthorized)")
    void testForceReturnBook_Unauthorized() throws Exception {
        var requestBuilder = post("/book/return/force?id=1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "ignat@gmail.com", password = "center", authorities = "USER")
    @DisplayName("Validation test")
    void testValidation() throws Exception {
        var requestNoAuthor = post("/book/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"author\":\"\",\"title\":\"Effective Java\"}");
        mockMvc.perform(requestNoAuthor)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Write down the author of the book!"));

        var requestNoUserName = post("/book/give")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Effective Java\",\"username\":\"\"}");
        mockMvc.perform(requestNoUserName)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Write down the username of the person to who you want to give your book!"));

        var requestNoTitle = post("/book/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\"}");
        mockMvc.perform(requestNoTitle)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Write down the title of the book you want to return!"));

        var requestNoProfileName = patch("/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}");
        mockMvc.perform(requestNoProfileName)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Write down your name!"));

        var requestNoAuthority = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name":"vlad",
                        "email":"ignat@gmail.com",
                        "password":"center"
                        }""");
        mockMvc.perform(requestNoAuthority)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Write down your authority!"));
    }
}
