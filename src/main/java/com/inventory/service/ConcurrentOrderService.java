package com.inventory.service;

import com.inventory.model.Order;
import com.inventory.producerconsumer.OrderConsumer;
import com.inventory.producerconsumer.OrderProducer;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ConcurrentOrderService {

    private final OrderService orderService;

    public ConcurrentOrderService() {
        this(new OrderService());
    }

    public ConcurrentOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void simulateConcurrentOrders() {
        int numberOfOrders = 50;
        int numberOfConsumers = 10;

        BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
        Semaphore semaphore = new Semaphore(3);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfConsumers + 1);

        Future<?> producerFuture = executor.submit(new OrderProducer(queue, numberOfOrders));

        for (int i = 0; i < numberOfConsumers; i++) {
            executor.submit(new OrderConsumer(queue, orderService, semaphore));
        }

        try {
            producerFuture.get();
            for (int i = 0; i < numberOfConsumers; i++) {
                queue.put(createPoisonPill());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException exception) {
            throw new RuntimeException("Producer failed.", exception);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Concurrent order simulation completed.");
    }

    private Order createPoisonPill() {
        return new Order(-1, -1, -1, LocalDateTime.now(), "STOP");
    }
}

