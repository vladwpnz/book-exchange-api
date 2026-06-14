package com.friends.sharing.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;
    private String author;
    private String title;
    @ManyToOne
    @JoinColumn(name = "holder_id")
    private User holder;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_book_id")
    private BookCatalog catalogBook;
}
