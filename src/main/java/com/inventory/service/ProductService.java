package com.inventory.service;

import com.inventory.dao.ProductDao;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.Product;
import com.inventory.util.ValidationUtil;
import java.util.List;

public class ProductService {

    private final ProductDao productDao;

    public ProductService() {
        this(new ProductDao());
    }

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void addProduct(Product product) {
        ValidationUtil.validateProduct(product);
        productDao.addProduct(product);
    }

    public void updateProduct(Product product) {
        ValidationUtil.validateProduct(product);
        ensureProductExists(product.getProductId());
        productDao.updateProduct(product);
    }

    public void deleteProduct(int productId) {
        ensureProductExists(productId);
        productDao.deleteProduct(productId);
    }

    public Product getProductById(int productId) {
        return ensureProductExists(productId);
    }

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public List<Product> searchByName(String name) {
        ValidationUtil.validateRequiredText(name, "Search name is required.");
        return productDao.findAll()
                .stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public List<Product> searchByCategory(String category) {
        ValidationUtil.validateRequiredText(category, "Category is required.");
        return productDao.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public Product searchByIdUsingStream(int productId) {
        return productDao.findAll()
                .stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
    }

    private Product ensureProductExists(int productId) {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
        return product;
    }
}

