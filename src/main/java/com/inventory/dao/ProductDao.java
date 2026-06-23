package com.inventory.dao;

import com.inventory.config.DatabaseConfig;
import com.inventory.exception.DatabaseException;
import com.inventory.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public void addProduct(Product product) {
        String sql = """
                INSERT INTO products(name, category, quantity, price)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setProductStatement(product, ps);
            ps.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to add product.", exception);
        }
    }

    public void updateProduct(Product product) {
        String sql = """
                UPDATE products
                SET name = ?, category = ?, quantity = ?, price = ?
                WHERE product_id = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setProductStatement(product, ps);
            ps.setInt(5, product.getProductId());
            ps.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to update product.", exception);
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete product.", exception);
        }
    }

    public Product findById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load product.", exception);
        }
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY product_id";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
            return products;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load products.", exception);
        }
    }

    private void setProductStatement(Product product, PreparedStatement ps) throws SQLException {
        ps.setString(1, product.getName());
        ps.setString(2, product.getCategory());
        ps.setInt(3, product.getQuantity());
        ps.setBigDecimal(4, product.getPrice());
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price")
        );
    }
}

