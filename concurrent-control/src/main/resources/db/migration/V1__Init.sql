CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
    );

CREATE TABLE IF NOT EXISTS products
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    quantity INTEGER,
    price DECIMAL(19, 2),
    version INTEGER,
    user_id BIGINT
    );

INSERT INTO users(username, password, name, email, role, enabled)
VALUES ('user2', 'pass456', 'User Two', 'user2@example.com', 'USER', true);

INSERT INTO users(username, password, name, email, role, enabled)
VALUES ('user3', 'pass789', 'User Three', 'user3@example.com', 'USER', true);

INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('Apple iPhone 12', 100, 699.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('Samsung Galaxy S21', 200, 799.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('Google Pixel 6', 150, 599.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('Nokia 3310', 400, 49.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('Sony Xperia 5', 250, 949.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('LG Velvet', 300, 399.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('OnePlus 9', 350, 729.99, 0, 1);
INSERT INTO products(name, quantity, price, version, user_id)
VALUES ('Motorola Moto G', 450, 199.99, 0, 1);

UPDATE products
SET user_id = 1
WHERE name in ('Samsung Galaxy S21', 'OnePlus 9');
UPDATE products
SET user_id = 2
WHERE name in ('Google Pixel 6', 'Motorola Moto G');