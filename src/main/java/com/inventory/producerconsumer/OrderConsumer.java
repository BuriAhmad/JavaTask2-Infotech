package com.inventory.producerconsumer;

import com.inventory.model.Order;
import com.inventory.service.OrderService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class OrderConsumer implements Runnable {

    private final BlockingQueue<Order> queue;
    private final OrderService orderService;
    private final Semaphore inventorySemaphore;

    public OrderConsumer(BlockingQueue<Order> queue,
                         OrderService orderService,
                         Semaphore inventorySemaphore) {
        this.queue = queue;
        this.orderService = orderService;
        this.inventorySemaphore = inventorySemaphore;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = queue.take();
                if ("STOP".equals(order.getStatus())) {
                    System.out.println(Thread.currentThread().getName() + " stopping.");
                    break;
                }

                inventorySemaphore.acquire();
                try {
                    boolean success = orderService.processOrder(order.getProductId(), order.getQuantity());
                    if (success) {
                        System.out.println(Thread.currentThread().getName()
                                + " processed order for product " + order.getProductId());
                    } else {
                        System.out.println(Thread.currentThread().getName()
                                + " failed order for product " + order.getProductId()
                                + " due to insufficient stock.");
                    }
                } finally {
                    inventorySemaphore.release();
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}

