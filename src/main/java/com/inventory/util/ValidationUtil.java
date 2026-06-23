package com.inventory.util;

import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import java.math.BigDecimal;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static void validateProduct(Product product) {
        if (product == null) {
            throw new ValidationException("Product is required.");
        }
        validateRequiredText(product.getName(), "Product name is required.");
        validateRequiredText(product.getCategory(), "Category is required.");
        if (product.getQuantity() < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }
        validatePositivePrice(product.getPrice());
    }

    public static void validatePositiveQuantity(int quantity, String fieldName) {
        if (quantity <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
    }

    public static void validateRequiredText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    public static void validatePositivePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than zero.");
        }
    }
}

