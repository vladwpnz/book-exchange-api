CREATE TABLE IF NOT EXISTS book_catalog (
    catalog_book_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre VARCHAR(120),
    description VARCHAR(1000),
    cover_url VARCHAR(512),
    isbn VARCHAR(32)
) ENGINE=InnoDB;

CREATE INDEX idx_book_catalog_title ON book_catalog (title);
CREATE INDEX idx_book_catalog_author ON book_catalog (author);
