package com.inventory.service;

import com.inventory.config.DatabaseConfig;
import com.inventory.dao.InventoryLogDao;
import com.inventory.dao.OrderDao;
import com.inventory.exception.DatabaseException;
import com.inventory.model.InventoryLog;
import com.inventory.model.Order;
import com.inventory.model.enums.InventoryAction;
import com.inventory.model.enums.OrderStatus;
import com.inventory.util.ValidationUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderService {

    private final OrderDao orderDao;
    private final InventoryLogDao inventoryLogDao;

    public OrderService() {
        this(new OrderDao(), new InventoryLogDao());
    }

    public OrderService(OrderDao orderDao, InventoryLogDao inventoryLogDao) {
        this.orderDao = orderDao;
        this.inventoryLogDao = inventoryLogDao;
    }

    public boolean processOrder(int productId, int orderQuantity) {
        ValidationUtil.validatePositiveQuantity(orderQuantity, "Order quantity");
        Connection connection = null;

        try {
            connection = DatabaseConfig.getConnection();
            connection.setAutoCommit(false);

            String updateStockSql = """
                    UPDATE products
                    SET quantity = quantity - ?
                    WHERE product_id = ?
                    AND quantity >= ?
                    """;

            try (PreparedStatement ps = connection.prepareStatement(updateStockSql)) {
                ps.setInt(1, orderQuantity);
                ps.setInt(2, productId);
                ps.setInt(3, orderQuantity);

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated == 0) {
                    connection.rollback();
                    return false;
                }
            }

            Order order = new Order(
                    0,
                    productId,
                    orderQuantity,
                    LocalDateTime.now(),
                    OrderStatus.COMPLETED.name()
            );
            orderDao.addOrder(connection, order);

            InventoryLog log = new InventoryLog(
                    0,
                    productId,
                    InventoryAction.STOCK_OUT.name(),
                    orderQuantity,
                    LocalDateTime.now()
            );
            inventoryLogDao.addLog(connection, log);

            connection.commit();
            return true;
        } catch (SQLException exception) {
            rollbackQuietly(connection);
            throw new DatabaseException("Failed to process order.", exception);
        } finally {
            resetAutoCommitAndClose(connection);
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void resetAutoCommitAndClose(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}

