package com.inventory.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLog {
    private int logId;
    private int productId;
    private String action;
    private int quantity;
    private LocalDateTime logTime;
}

