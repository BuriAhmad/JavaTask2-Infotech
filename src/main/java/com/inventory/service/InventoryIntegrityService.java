package com.inventory.service;

import com.inventory.dao.ProductDao;
import com.inventory.model.Product;
import java.util.List;

public class InventoryIntegrityService {

    private final ProductDao productDao;

    public InventoryIntegrityService() {
        this(new ProductDao());
    }

    public InventoryIntegrityService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void repairNegativeQuantitiesOnStartup() {
        List<Product> negativeProducts = productDao.findNegativeQuantityProducts();
        if (negativeProducts.isEmpty()) {
            System.out.println("Startup integrity check: no negative product quantities found.");
            return;
        }

        System.out.println("Startup integrity check: found negative product quantities:");
        for (Product product : negativeProducts) {
            System.out.printf("ID: %d | Name: %s | Quantity: %d%n",
                    product.getProductId(),
                    product.getName(),
                    product.getQuantity());
            productDao.setQuantity(product.getProductId(), 0);
        }

        System.out.println("Startup integrity repair: all negative product quantities were reset to 0.");
    }
}
