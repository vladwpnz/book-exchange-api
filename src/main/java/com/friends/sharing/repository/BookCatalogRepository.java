package com.friends.sharing.repository;

import com.friends.sharing.model.BookCatalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCatalogRepository extends JpaRepository<BookCatalog, Long> {
    @Query("""
            SELECT bookCatalog FROM BookCatalog bookCatalog
            WHERE LOWER(bookCatalog.title) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(bookCatalog.author) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    List<BookCatalog> searchByTitleOrAuthor(@Param("query") String query);
}
