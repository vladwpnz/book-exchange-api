package com.friends.sharing.configuration;

import com.friends.sharing.dto.response.*;
import com.friends.sharing.model.Book;
import com.friends.sharing.model.BookCatalog;
import com.friends.sharing.model.Present;
import com.friends.sharing.model.User;

public class Mapper {
    public static UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .authority(user.getAuthority())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public static BookWithUserDTO mapToBookWithUserDTO(Book book, User user) {
        return BookWithUserDTO.builder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .person(mapToUserDTO(user))
                .build();
    }

    /*public static PresentWithUserDTO mapToPresentWithUserDTO(Present present, User user) {
        return PresentWithUserDTO.builder()
                .box_color(present.getBox_color())
                .content(present.getContent())
                .person(mapToUserDTO(user))
                .build();
    }*/

    public static BookDTO mapToBookDTO(Book book) {
        return BookDTO.builder()
                .book_id(book.getBook_id())
                .author(book.getAuthor())
                .title(book.getTitle())
                .holder_id(book.getHolder().getUser_id())
                .owner_id(book.getOwner().getUser_id())
                .build();
    }

    public static BookCatalogDTO mapToBookCatalogDTO(BookCatalog bookCatalog) {
        return BookCatalogDTO.builder()
                .catalogBookId(bookCatalog.getCatalogBookId())
                .title(bookCatalog.getTitle())
                .author(bookCatalog.getAuthor())
                .genre(bookCatalog.getGenre())
                .description(bookCatalog.getDescription())
                .coverUrl(bookCatalog.getCoverUrl())
                .isbn(bookCatalog.getIsbn())
                .build();
    }

    /*public static PresentDTO mapToPresentDTO(Present present) {
        return PresentDTO.builder()
                .present_id(present.getPresent_id())
                .box_color(present.getBox_color())
                .content(present.getContent())
                .holder_id(present.getHolder().getUser_id())
                .owner_id(present.getOwner().getUser_id())
                .build();
    }*/
}
