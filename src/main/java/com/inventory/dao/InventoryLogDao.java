package com.inventory.dao;

import com.inventory.config.DatabaseConfig;
import com.inventory.exception.DatabaseException;
import com.inventory.model.InventoryLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogDao {

    public void addLog(InventoryLog log) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            addLog(connection, log);
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to create inventory log.", exception);
        }
    }

    public void addLog(Connection connection, InventoryLog log) throws SQLException {
        String sql = """
                INSERT INTO inventory_logs(product_id, action, quantity, log_time)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, log.getProductId());
            ps.setString(2, log.getAction());
            ps.setInt(3, log.getQuantity());
            ps.setTimestamp(4, Timestamp.valueOf(log.getLogTime()));
            ps.executeUpdate();
        }
    }

    public List<InventoryLog> findAll() {
        String sql = "SELECT * FROM inventory_logs ORDER BY log_id";
        List<InventoryLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(mapRow(rs));
            }
            return logs;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load inventory logs.", exception);
        }
    }

    public List<InventoryLog> findByProductId(int productId) {
        String sql = "SELECT * FROM inventory_logs WHERE product_id = ? ORDER BY log_id";
        List<InventoryLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
            return logs;
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load inventory logs for product.", exception);
        }
    }

    private InventoryLog mapRow(ResultSet rs) throws SQLException {
        return new InventoryLog(
                rs.getInt("log_id"),
                rs.getInt("product_id"),
                rs.getString("action"),
                rs.getInt("quantity"),
                rs.getTimestamp("log_time").toLocalDateTime()
        );
    }
}

