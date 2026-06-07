CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    authority TINYINT,
    CONSTRAINT chk_user_authority CHECK (authority BETWEEN 0 AND 1)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS books (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author VARCHAR(255),
    title VARCHAR(255),
    holder_id BIGINT,
    owner_id BIGINT,
    CONSTRAINT fk_books_holder_user FOREIGN KEY (holder_id) REFERENCES `user` (user_id),
    CONSTRAINT fk_books_owner_user FOREIGN KEY (owner_id) REFERENCES `user` (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS presents (
    present_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    box_color VARCHAR(255),
    content VARCHAR(255),
    holder_id BIGINT,
    owner_id BIGINT,
    CONSTRAINT fk_presents_holder_user FOREIGN KEY (holder_id) REFERENCES `user` (user_id),
    CONSTRAINT fk_presents_owner_user FOREIGN KEY (owner_id) REFERENCES `user` (user_id)
) ENGINE=InnoDB;
