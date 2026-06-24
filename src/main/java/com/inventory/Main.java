package com.inventory;

import com.inventory.service.InventoryIntegrityService;
import com.inventory.ui.Menu;

public class Main {

    public static void main(String[] args) {
        new InventoryIntegrityService().repairNegativeQuantitiesOnStartup();
        new Menu().start();
    }
}
