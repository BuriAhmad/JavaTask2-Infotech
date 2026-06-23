package com.inventory.service;

import com.inventory.dao.ProductDao;
import com.inventory.model.Product;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private final ProductDao productDao;

    public ReportService() {
        this(new ProductDao());
    }

    public ReportService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getTop5ExpensiveProducts() {
        return productDao.findAll()
                .stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .limit(5)
                .toList();
    }

    public Map<String, List<Product>> getProductsByCategory() {
        return productDao.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory));
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productDao.findAll()
                .stream()
                .filter(product -> product.getQuantity() < threshold)
                .toList();
    }

    public BigDecimal getTotalInventoryValue() {
        return productDao.findAll()
                .stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

