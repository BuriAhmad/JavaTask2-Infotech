package com.inventory.service;

import com.inventory.dao.InventoryLogDao;
import com.inventory.dao.ProductDao;
import com.inventory.exception.InsufficientStockException;
import com.inventory.model.InventoryLog;
import com.inventory.model.Product;
import com.inventory.model.enums.InventoryAction;
import com.inventory.util.ValidationUtil;
import java.time.LocalDateTime;

public class InventoryService {

    private final ProductDao productDao;
    private final InventoryLogDao inventoryLogDao;

    public InventoryService() {
        this(new ProductDao(), new InventoryLogDao());
    }

    public InventoryService(ProductDao productDao, InventoryLogDao inventoryLogDao) {
        this.productDao = productDao;
        this.inventoryLogDao = inventoryLogDao;
    }

    public void stockIn(int productId, int quantity) {
        ValidationUtil.validatePositiveQuantity(quantity, "Stock in quantity");
        Product product = requireProduct(productId);
        product.setQuantity(product.getQuantity() + quantity);
        productDao.updateProduct(product);
        inventoryLogDao.addLog(new InventoryLog(
                0,
                productId,
                InventoryAction.STOCK_IN.name(),
                quantity,
                LocalDateTime.now()
        ));
    }

    public void stockOut(int productId, int quantity) {
        ValidationUtil.validatePositiveQuantity(quantity, "Stock out quantity");
        Product product = requireProduct(productId);
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
        product.setQuantity(product.getQuantity() - quantity);
        productDao.updateProduct(product);
        inventoryLogDao.addLog(new InventoryLog(
                0,
                productId,
                InventoryAction.STOCK_OUT.name(),
                quantity,
                LocalDateTime.now()
        ));
    }

    private Product requireProduct(int productId) {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new com.inventory.exception.ProductNotFoundException("Product not found: " + productId);
        }
        return product;
    }
}

