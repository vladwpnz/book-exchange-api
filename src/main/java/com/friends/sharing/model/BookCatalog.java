package com.friends.sharing.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_catalog")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catalog_book_id")
    private Long catalogBookId;

    private String title;
    private String author;
    private String genre;
    private String description;

    @Column(name = "cover_url")
    private String coverUrl;

    private String isbn;
}
