ALTER TABLE books
    ADD COLUMN catalog_book_id BIGINT NULL,
    ADD CONSTRAINT fk_books_book_catalog
        FOREIGN KEY (catalog_book_id) REFERENCES book_catalog (catalog_book_id),
    ADD CONSTRAINT uq_books_owner_catalog_book UNIQUE (owner_id, catalog_book_id);
