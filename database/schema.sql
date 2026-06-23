DROP DATABASE IF EXISTS inventory_system;
CREATE DATABASE inventory_system;
USE inventory_system;

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_orders_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);

CREATE TABLE inventory_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    action VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    log_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_logs_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);

WITH RECURSIVE numbers AS (
    SELECT 1 n
    UNION ALL
    SELECT n + 1
    FROM numbers
    WHERE n < 1000
)
INSERT INTO products(name, category, quantity, price)
SELECT
    CONCAT('Product_', n),
    CASE MOD(n, 5)
        WHEN 0 THEN 'Electronics'
        WHEN 1 THEN 'Books'
        WHEN 2 THEN 'Food'
        WHEN 3 THEN 'Clothing'
        ELSE 'Hardware'
    END,
    FLOOR(RAND() * 500) + 20,
    ROUND((RAND() * 1000) + 10, 2)
FROM numbers;

WITH RECURSIVE numbers AS (
    SELECT 1 n
    UNION ALL
    SELECT n + 1
    FROM numbers
    WHERE n < 1000
)
INSERT INTO orders(product_id, quantity, order_date, status)
SELECT
    FLOOR(RAND() * 1000) + 1,
    FLOOR(RAND() * 10) + 1,
    NOW() - INTERVAL FLOOR(RAND() * 365) DAY,
    CASE MOD(n, 3)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'PENDING'
        ELSE 'CANCELLED'
    END
FROM numbers;

WITH RECURSIVE numbers AS (
    SELECT 1 n
    UNION ALL
    SELECT n + 1
    FROM numbers
    WHERE n < 1000
)
INSERT INTO inventory_logs(product_id, action, quantity, log_time)
SELECT
    FLOOR(RAND() * 1000) + 1,
    CASE MOD(n, 2)
        WHEN 0 THEN 'STOCK_IN'
        ELSE 'STOCK_OUT'
    END,
    FLOOR(RAND() * 25) + 1,
    NOW() - INTERVAL FLOOR(RAND() * 365) DAY
FROM numbers;

UPDATE products
SET quantity = -3
WHERE product_id = 173;

UPDATE products
SET quantity = -7
WHERE product_id = 542;

UPDATE products
SET quantity = -1
WHERE product_id = 913;

UPDATE products
SET name = 'Wireless Mouse'
WHERE product_id IN (120, 451, 782);

INSERT INTO orders(product_id, quantity, order_date, status)
VALUES
    (843, 150, '2025-01-01', 'COMPLETED'),
    (843, 120, '2025-01-05', 'COMPLETED'),
    (843, 80, '2025-01-07', 'COMPLETED');

INSERT INTO inventory_logs(product_id, action, quantity, log_time)
VALUES (555, 'STOCK_OUT', 500, NOW());

