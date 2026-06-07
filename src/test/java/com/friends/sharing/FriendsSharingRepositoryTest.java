package com.friends.sharing;

import com.friends.sharing.configuration.security.Authorities;
import com.friends.sharing.model.Book;
import com.friends.sharing.model.Present;
import com.friends.sharing.model.User;
import com.friends.sharing.repository.BookRepository;
import com.friends.sharing.repository.PresentRepository;
import com.friends.sharing.repository.UserRepository;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class FriendsSharingRepositoryTest {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PresentRepository presentRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Test for save() method in UserRepository")
    void saveTest_UserRepository() {
        User expect = User.builder()
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();
        userRepository.save(expect);

        entityManager.clear(); //to clear cache

        Optional<User> actual = userRepository.findById(expect.getUser_id());
        assertThat(actual)
                .isPresent()
                .get()
                .isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for findByEmail() method in UserRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0)"})
    void findByEmailTest_UserRepository() {
        User expect = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        Optional<User> actual = userRepository.findUserByEmail("email@gmail.com");
        assertThat(actual)
                .isPresent()
                .get()
                .isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for save() method in BookRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0)"})
    void saveTest_BookRepository() {
        var user = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        var expect = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .holder(user)
                .owner(user)
                .build();
        bookRepository.saveAndFlush(expect);

        entityManager.clear(); //to clear cache

        Optional<Book> actual =  bookRepository.findById(expect.getBook_id());
        assertThat(actual)
                .isPresent()
                .get()
                .isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for findHeldBooks() method in BookRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0), (2, 'vlad', 'gmail@gmail.com', '1234', 0)",
            "INSERT INTO books(book_id, title, author, holder_id, owner_id) " +
                "VALUES (1, 'Effective Java', 'Joshua Bloch', 1, 1)," +
                    "(2, 'Not Effective Java', 'SomeOne', 2, 1), (3, 'Python', 'Hahaha', 2, 2)"})
    void findHeldBooksTest_BookRepository() {
        var user = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        var expect = Book.builder()
                .book_id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .holder(user)
                .owner(user)
                .build();

        List<Book> actual = bookRepository.findHeldBooks(1L);
        assertThat(actual)
                .hasOnlyElementsOfType(Book.class)
                .hasSize(1)
                .contains(expect, Index.atIndex(0));
    }

    @Test
    @DisplayName("Test for findOwnedBooks() method in BookRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
                "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0), (2, 'vlad', 'gmail@gmail.com', '1234', 0)",
            "INSERT INTO books(book_id, title, author, holder_id, owner_id) " +
                "VALUES (1, 'Effective Java', 'Joshua Bloch', 1, 1)," +
                    "(2, 'Not Effective Java', 'SomeOne', 1, 1), (3, 'Python', 'Hahaha', 2, 2)"})
    void findOwnedBookTest_BookRepository() {
        var user = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        var expectAtIndexZero = Book.builder()
                .book_id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .holder(user)
                .owner(user)
                .build();
        var expectAtIndexOne = Book.builder()
                .book_id(2L)
                .title("Not Effective Java")
                .author("SomeOne")
                .holder(user)
                .owner(user)
                .build();

        List<Book> actual = bookRepository.findOwnedBooks(1L);
        assertThat(actual)
                .hasOnlyElementsOfType(Book.class)
                .hasSize(2)
                .contains(expectAtIndexZero, Index.atIndex(0))
                .contains(expectAtIndexOne, Index.atIndex(1));
    }

    /*@Test
    @DisplayName("Test for save() method in PresentRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
            "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0)"})
    void saveTest_PresentRepository() {
        var user = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        var expect = Present.builder()
                .box_color("red")
                .content("books")
                .holder(user)
                .owner(user)
                .build();
        presentRepository.saveAndFlush(expect);

        entityManager.clear(); //to clear cache

        Optional<Present> actual = presentRepository.findById(expect.getPresent_id());
        assertThat(actual)
                .isPresent()
                .get()
                .isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for findHeldPresents() method in PresentRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
                "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0), (2, 'vlad', 'gmail@gmail.com', '1234', 0)",
            "INSERT INTO presents(present_id, box_color, content, user_id, owner_id) " +
                "VALUES (1, 'red', 'books', 1, 1),(2, 'black', 'dog', 2, 1), (3, 'white', 'colors', 2, 2)"})
    void findHeldPresentsTest_PresentRepository() {
        var user = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        var expect = Present.builder()
                .present_id(1L)
                .box_color("red")
                .content("books")
                .holder(user)
                .owner(user)
                .build();

        List<Present> actual = presentRepository.findHeldPresents(1L);
        assertThat(actual)
                .hasOnlyElementsOfType(Present.class)
                .hasSize(1)
                .contains(expect, Index.atIndex(0));
    }

    @Test
    @DisplayName("Test for findOwnedPresents() method in PresentRepository")
    @Sql(statements = {"INSERT INTO user(user_id, name, email, password, authority) " +
                "VALUES (1, 'vadim', 'email@gmail.com', '1234', 0), (2, 'vlad', 'gmail@gmail.com', '1234', 0)",
            "INSERT INTO presents(present_id, box_color, content, user_id, owner_id) " +
                "VALUES (1, 'red', 'books', 1, 1),(2, 'black', 'dog', 1, 1), (3, 'white', 'colors', 2, 2)"})
    void findOwnedPresentsTest_PresentRepository() {
        var user = User.builder()
                .user_id(1L)
                .name("vadim")
                .email("email@gmail.com")
                .password("1234")
                .authority(Authorities.USER)
                .build();

        var expectAtIndexZero = Present.builder()
                .present_id(1L)
                .box_color("red")
                .content("books")
                .holder(user)
                .owner(user)
                .build();
        var expectAtIndexOne = Present.builder()
                .present_id(2L)
                .box_color("black")
                .content("dog")
                .holder(user)
                .owner(user)
                .build();

        List<Present> actual = presentRepository.findOwnedPresents(1L);
        assertThat(actual)
                .hasOnlyElementsOfType(Present.class)
                .hasSize(2)
                .contains(expectAtIndexZero, Index.atIndex(0))
                .contains(expectAtIndexOne, Index.atIndex(1));
    }*/
}
