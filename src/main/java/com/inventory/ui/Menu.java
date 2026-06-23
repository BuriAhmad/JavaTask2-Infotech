package com.inventory.ui;

import com.inventory.exception.DatabaseException;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import com.inventory.service.ConcurrentOrderService;
import com.inventory.service.InventoryService;
import com.inventory.service.OrderService;
import com.inventory.service.ProductService;
import com.inventory.service.ReportService;
import com.inventory.util.InputUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Menu {

    private final ProductService productService = new ProductService();
    private final InventoryService inventoryService = new InventoryService();
    private final OrderService orderService = new OrderService();
    private final ReportService reportService = new ReportService();
    private final ConcurrentOrderService concurrentOrderService = new ConcurrentOrderService();

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputUtil.readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> updateProduct();
                    case 3 -> deleteProduct();
                    case 4 -> searchProduct();
                    case 5 -> viewAllProducts();
                    case 6 -> stockIn();
                    case 7 -> stockOut();
                    case 8 -> placeOrder();
                    case 9 -> showReportsMenu();
                    case 10 -> concurrentOrderService.simulateConcurrentOrders();
                    case 0 -> {
                        running = false;
                        System.out.println("Exiting application.");
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (ValidationException | ProductNotFoundException | InsufficientStockException exception) {
                System.out.println(exception.getMessage());
            } catch (DatabaseException exception) {
                System.out.println("Database error occurred: " + exception.getMessage());
            } catch (RuntimeException exception) {
                System.out.println("Unexpected error: " + exception.getMessage());
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("========== Inventory Management System ==========");
        System.out.println("1. Add Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("4. Search Product");
        System.out.println("5. View All Products");
        System.out.println("6. Stock In");
        System.out.println("7. Stock Out");
        System.out.println("8. Place Order");
        System.out.println("9. Reports");
        System.out.println("10. Simulate 50 Concurrent Orders");
        System.out.println("0. Exit");
        System.out.println();
    }

    private void addProduct() {
        Product product = buildProduct(0, false);
        productService.addProduct(product);
        System.out.println("Product added successfully.");
    }

    private void updateProduct() {
        int productId = InputUtil.readInt("Enter product ID to update: ");
        Product existing = productService.getProductById(productId);
        Product updated = buildProduct(existing.getProductId(), true);
        productService.updateProduct(updated);
        System.out.println("Product updated successfully.");
    }

    private void deleteProduct() {
        int productId = InputUtil.readInt("Enter product ID to delete: ");
        productService.deleteProduct(productId);
        System.out.println("Product deleted successfully.");
    }

    private void searchProduct() {
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Search by Category");
        int choice = InputUtil.readInt("Enter search option: ");

        switch (choice) {
            case 1 -> printProduct(productService.searchByIdUsingStream(
                    InputUtil.readInt("Enter product ID: ")));
            case 2 -> printProducts(productService.searchByName(InputUtil.readString("Enter product name: ")));
            case 3 -> printProducts(productService.searchByCategory(InputUtil.readString("Enter category: ")));
            default -> System.out.println("Invalid search option.");
        }
    }

    private void viewAllProducts() {
        printProducts(productService.getAllProducts());
    }

    private void stockIn() {
        int productId = InputUtil.readInt("Enter product ID: ");
        int quantity = InputUtil.readInt("Enter quantity to stock in: ");
        inventoryService.stockIn(productId, quantity);
        System.out.println("Stock in completed successfully.");
    }

    private void stockOut() {
        int productId = InputUtil.readInt("Enter product ID: ");
        int quantity = InputUtil.readInt("Enter quantity to stock out: ");
        inventoryService.stockOut(productId, quantity);
        System.out.println("Stock out completed successfully.");
    }

    private void placeOrder() {
        int productId = InputUtil.readInt("Enter product ID: ");
        int quantity = InputUtil.readInt("Enter order quantity: ");
        boolean success = orderService.processOrder(productId, quantity);
        if (success) {
            System.out.println("Order completed successfully.");
        } else {
            System.out.println("Order failed. Insufficient stock or product not found.");
        }
    }

    private void showReportsMenu() {
        boolean showing = true;
        while (showing) {
            System.out.println();
            System.out.println("========== Reports ==========");
            System.out.println("1. Top 5 Expensive Products");
            System.out.println("2. Products By Category");
            System.out.println("3. Low Stock Products");
            System.out.println("4. Total Inventory Value");
            System.out.println("0. Back");

            int choice = InputUtil.readInt("Enter report choice: ");
            switch (choice) {
                case 1 -> printProducts(reportService.getTop5ExpensiveProducts());
                case 2 -> printCategoryReport(reportService.getProductsByCategory());
                case 3 -> printProducts(reportService.getLowStockProducts(10));
                case 4 -> System.out.println("Total inventory value: " + reportService.getTotalInventoryValue());
                case 0 -> showing = false;
                default -> System.out.println("Invalid report option.");
            }
        }
    }

    private Product buildProduct(int productId, boolean updating) {
        String name = InputUtil.readString(updating ? "Enter new product name: " : "Enter product name: ");
        String category = InputUtil.readString(updating ? "Enter new category: " : "Enter category: ");
        int quantity = InputUtil.readInt(updating ? "Enter new quantity: " : "Enter quantity: ");
        BigDecimal price = InputUtil.readBigDecimal(updating ? "Enter new price: " : "Enter price: ");
        return new Product(productId, name, category, quantity, price);
    }

    private void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        products.forEach(this::printProduct);
    }

    private void printProduct(Product product) {
        System.out.printf("ID: %d | Name: %s | Category: %s | Quantity: %d | Price: %s%n",
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice());
    }

    private void printCategoryReport(Map<String, List<Product>> groupedProducts) {
        if (groupedProducts.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        groupedProducts.forEach((category, products) ->
                System.out.println(category + ": " + products.size() + " product(s)"));
    }
}
