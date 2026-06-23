package com.inventory.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private int orderId;
    private int productId;
    private int quantity;
    private LocalDateTime orderDate;
    private String status;
}

