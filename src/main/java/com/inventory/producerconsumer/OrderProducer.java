package com.inventory.producerconsumer;

import com.inventory.model.Order;
import com.inventory.model.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class OrderProducer implements Runnable {

    private final BlockingQueue<Order> queue;
    private final int numberOfOrders;
    private final Random random = new Random();

    public OrderProducer(BlockingQueue<Order> queue, int numberOfOrders) {
        this.queue = queue;
        this.numberOfOrders = numberOfOrders;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < numberOfOrders; i++) {
                Order order = new Order(
                        0,
                        random.nextInt(1000) + 1,
                        random.nextInt(10) + 1,
                        LocalDateTime.now(),
                        OrderStatus.PENDING.name()
                );
                queue.put(order);
                System.out.println("Produced order: productId=" + order.getProductId()
                        + ", qty=" + order.getQuantity());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}

