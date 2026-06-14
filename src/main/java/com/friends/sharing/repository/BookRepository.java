package com.friends.sharing.repository;

import com.friends.sharing.model.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("""
            SELECT CASE WHEN COUNT(book) > 0 THEN true ELSE false END
            FROM Book book
            WHERE book.owner.user_id = :ownerId
              AND book.catalogBook.catalogBookId = :catalogBookId
            """)
    boolean existsByOwnerIdAndCatalogBookId(@Param("ownerId") Long ownerId,
                                            @Param("catalogBookId") Long catalogBookId);

    default List<Book> findHeldBooks(Long id) {
        return findAll().stream()
                .filter(book -> id.equals(book.getHolder().getUser_id()))
                .toList();
    }

    default List<Book> findOwnedBooks(Long id) {
        return findAll().stream()
                .filter(book -> id.equals(book.getOwner().getUser_id()))
                .toList();
    }
}
