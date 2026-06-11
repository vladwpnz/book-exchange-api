package com.friends.sharing.service;

import com.friends.sharing.configuration.Mapper;
import com.friends.sharing.dto.request.*;
import com.friends.sharing.dto.response.*;
import com.friends.sharing.exception.ItemException;
import com.friends.sharing.model.Book;
import com.friends.sharing.model.Present;
import com.friends.sharing.model.User;
import com.friends.sharing.repository.BookRepository;
import com.friends.sharing.repository.PresentRepository;
import com.friends.sharing.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendsSharingService {
    private final BookRepository bookRepository;
    private final PresentRepository presentRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookWithUserDTO addBook(AddBookRequest bookRequest, User user) {
        Book book = Book.builder()
                .author(bookRequest.getAuthor())
                .title(bookRequest.getTitle())
                .holder(user)
                .owner(user)
                .build();
        bookRepository.save(book);

        return Mapper.mapToBookWithUserDTO(book, user);
    }

    /*@Transactional
    public PresentWithUserDTO addPresent(AddPresentRequest presentRequest, User user) {
        Present present = Present.builder()
                .box_color(presentRequest.getBox_color())
                .content(presentRequest.getContent())
                .holder(user)
                .owner(user)
                .build();
        presentRepository.save(present);

        return Mapper.mapToPresentWithUserDTO(present, user);
    }*/

    public UserDTO getProfile(User user) {
        return Mapper.mapToUserDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(UpdateProfileRequest request, User user) {
        user.setName(request.getName());
        User savedUser = userRepository.save(user);

        return Mapper.mapToUserDTO(savedUser);
    }

    public ItemsWithUser getHeldItems(User user) {
        List<BookWithUserDTO> books = bookRepository.findHeldBooks(user.getUser_id())
                .stream()
                .map(book -> Mapper.mapToBookWithUserDTO(book, book.getOwner()))
                .toList();

        /*List<PresentWithUserDTO> presents = presentRepository.findHeldPresents(user.getUser_id())
                .stream()
                .map(present -> Mapper.mapToPresentWithUserDTO(present, present.getOwner()))
                .toList();*/

        return new ItemsWithUser(books);//, presents);
    }

    public ItemsWithUser getOwnedItems(User user) {
        List<BookWithUserDTO> books = bookRepository.findOwnedBooks(user.getUser_id())
                .stream()
                .map(book -> Mapper.mapToBookWithUserDTO(book, book.getHolder()))
                .toList();

        /*List<PresentWithUserDTO> presents = presentRepository.findOwnedPresents(user.getUser_id())
                .stream()
                .map(present -> Mapper.mapToPresentWithUserDTO(present, present.getHolder()))
                .toList();*/

        return new ItemsWithUser(books);//, presents);
    }

    public Items getItems() {
        List<BookDTO> books = bookRepository.findAll()
                .stream()
                .map(Mapper::mapToBookDTO)
                .toList();

        /*List<PresentDTO> presents = presentRepository.findAll()
                .stream()
                .map(Mapper::mapToPresentDTO)
                .toList();*/

        return new Items(books);//, presents);
    }

    @Transactional
    public BookWithUserDTO shareBook(GiveBookRequest bookRequest, User owner) {
        Book book = bookRepository.findOwnedBooks(owner.getUser_id()).stream()
                .filter(b -> b.getTitle().equals(bookRequest.getTitle()))
                .findFirst().orElseThrow(() -> new ItemException("You do not own a book with that title!"));
        User holder = getNewUser(bookRequest.getUsername(), book);

        book.setHolder(holder);
        bookRepository.save(book);

        return Mapper.mapToBookWithUserDTO(book, holder);
    }

    @Transactional
    public BookWithUserDTO giveBook(GiveBookRequest bookRequest, User owner) {
        Book book = bookRepository.findOwnedBooks(owner.getUser_id()).stream()
                .filter(b -> b.getTitle().equals(bookRequest.getTitle()))
                .findFirst().orElseThrow(() -> new ItemException("You do not own a book with that title!"));
        User holderOwner = getNewUser(bookRequest.getUsername(), book);

        book.setHolder(holderOwner);
        book.setOwner(holderOwner);
        bookRepository.save(book);

        return Mapper.mapToBookWithUserDTO(book, holderOwner);
    }

    private User getNewUser(String username, Book book) {
        if (!book.getHolder().equals(book.getOwner())) {
            throw new ItemException("You have already given this book to someone!");
        }

        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new ItemException("There are no users with that username!!"));
    }

    /*@Transactional
    public PresentWithUserDTO givePresent(GivePresentRequest presentRequest, User owner) {
        User holder = userRepository.findUserByEmail(presentRequest.getUsername())
                .orElseThrow(() -> new ItemException("There are no users with that username!!"));
        Present present = presentRepository.findOwnedPresents(owner.getUser_id()).stream()
                .filter(p -> p.getBox_color().equals(presentRequest.getBox_color()))
                .findFirst().orElseThrow(() -> new ItemException("You have no present with this description!"));
        if (!present.getHolder().equals(owner)) {
            throw new ItemException("You have already given this present to someone!");
        }

        present.setHolder(holder);
        presentRepository.save(present);

        return Mapper.mapToPresentWithUserDTO(present, present.getHolder());
    }*/

    @Transactional
    public void returnBook(ReturnBookRequest bookRequest, User user) {
        Book book = bookRepository.findHeldBooks(user.getUser_id()).stream()
                .filter(b -> b.getTitle().equals(bookRequest.getTitle()))
                .findFirst().orElseThrow(() -> new ItemException("You do not hold a book with that title!"));
        User owner = book.getOwner();
        if (user.equals(owner)) {
            throw new ItemException("You are the owner of this book!");
        }

        book.setHolder(owner);
        bookRepository.save(book);
    }

    @Transactional
    public boolean deleteBook(Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            return false;
        }

        bookRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean forceReturnBook(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            return false;
        }
        Book book = optionalBook.get();

        book.setHolder(book.getOwner());
        bookRepository.save(book);

        return true;
    }

    /*@Transactional
    public boolean deletePresent(Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            return false;
        }

        bookRepository.deleteById(id);
        return true;
    }*/
}
