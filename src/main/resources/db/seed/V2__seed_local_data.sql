INSERT IGNORE INTO `user` (user_id, name, email, password, authority)
VALUES (1, 'vadim', 'email@gmail.com', '$2a$10$Hzdg8upvCxY8wqZAyq79Ou1szV6sS6Xy55GmDyOqgz8ZKbMsklZ1C', 1);

INSERT IGNORE INTO books (book_id, author, title, owner_id, holder_id)
VALUES (1, 'Joshua Bloch', 'Effective Java', 1, 1);

INSERT IGNORE INTO presents (present_id, box_color, content, owner_id, holder_id)
VALUES (1, 'red', 'books', 1, 1);
