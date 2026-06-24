# Inventory Management System

## Overview

This is a Java console-based inventory management system built using Maven, JDBC, MySQL, Java Streams, transactions, and multithreading.

## Features

- Product CRUD
- Product search by ID, name, and category
- Stock In
- Stock Out
- Inventory logs
- Order processing
- JDBC PreparedStatement usage
- Transaction handling
- Stream-based reports
- Top 5 expensive products
- Products grouped by category
- Low stock products
- Total inventory value
- Concurrent order simulation
- Producer-consumer pattern
- Semaphore-controlled inventory access
- BlockingQueue-based order queue

## Technologies Used

- Java 17
- Maven
- MySQL 8.4.x
- JDBC
- Lombok
- Java Streams
- ExecutorService
- Semaphore
- BlockingQueue

## Project Structure

```text
.
├── pom.xml
├── README.md
├── database/
│   └── schema.sql
└── src/
    └── main/
        └── java/
            └── com/
                └── inventory/
                    ├── Main.java
                    ├── config/
                    ├── dao/
                    ├── exception/
                    ├── model/
                    ├── producerconsumer/
                    ├── service/
                    ├── ui/
                    └── util/
```

## Database Setup

1. Install MySQL locally.
2. Open MySQL shell or Workbench.
3. Run `mysql -u root -p < database/schema.sql`.
4. Set `DB_URL`, `DB_USER`, and `DB_PASSWORD` if you are not using the default local connection.

By default, `DatabaseConfig` uses:

- `jdbc:mysql://localhost:3306/inventory_system`
- `root`
- empty password

## How to Run

Compile:

```bash
mvn clean compile
```

Run:

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

Or run `com.inventory.Main` directly from IntelliJ IDEA.

## Sample Menu

```text
========== Inventory Management System ==========
1. Add Product
2. Update Product
3. Delete Product
4. Search Product
5. View All Products
6. Stock In
7. Stock Out
8. Place Order
9. Reports
10. Simulate 50 Concurrent Orders
0. Exit
```

## Reports

The reports menu includes:

- Top 5 Expensive Products
- Products By Category
- Low Stock Products
- Total Inventory Value

These reports are implemented with Java Streams for sorting, filtering, grouping, and aggregation.

## Concurrency Explanation

The concurrency simulation creates 50 random orders and feeds them into a `LinkedBlockingQueue`. One producer thread generates those orders, while multiple consumer threads process them. A `Semaphore` limits inventory modification to three concurrent workers at a time.

Race-condition protection does not rely only on Java-side checks. The final order-processing flow uses an atomic SQL update:

```sql
UPDATE products
SET quantity = quantity - ?
WHERE product_id = ?
AND quantity >= ?;
```

That prevents overselling even when multiple threads attempt to buy the same product.

## Transaction Explanation

Order creation, inventory deduction, and inventory-log insertion happen inside one JDBC transaction using:

- `setAutoCommit(false)`
- `commit()`
- `rollback()`

If any step fails, the transaction is rolled back so partial order state is not saved.

## Notes

- No ORM frameworks are used.
- All database operations are implemented manually using JDBC and `PreparedStatement`.
- The sample SQL intentionally seeds a few products with negative quantities for edge-case reporting and validation checks.
- New stock-out and order-processing operations do not allow new negative inventory to be created.
