package com.inventory.dao;

import com.inventory.config.DatabaseConfig;
import com.inventory.exception.DatabaseException;
import com.inventory.model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    public void addOrder(Order order) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            addOrder(connection, order);
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to create order.", exception);
        }
    }

    public void addOrder(Connection connection, Order order) throws SQLException {
        String sql = """
                INSERT INTO orders(product_id, quantity, order_date, status)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, order.getProductId());
            ps.setInt(2, order.getQuantity());
            ps.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            ps.setString(4, order.getStatus());
            ps.executeUpdate();
        }
    }

    public Order findById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load order.", exception);
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY order_id";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
            return orders;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load orders.", exception);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getTimestamp("order_date").toLocalDateTime(),
                rs.getString("status")
        );
    }
}

