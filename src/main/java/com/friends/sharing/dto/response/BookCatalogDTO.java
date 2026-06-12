package com.friends.sharing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookCatalogDTO {
    private Long catalogBookId;
    private String title;
    private String author;
    private String genre;
    private String description;
    private String coverUrl;
    private String isbn;
}
