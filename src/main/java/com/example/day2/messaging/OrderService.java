package com.example.day2.messaging;

import com.example.day2.shop.Order;
import com.example.day2.shop.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Final state (step 4): the transactional outbox. The message is saved in the same transaction as
 * the order, and OutboxPublisher sends it afterwards. Step 2 (publish after the commit) is kept in
 * OrderMessagePublisher, as a reference.
 */
@Service
public class OrderService {

    private final OrderRepository orders;
    private final OutboxRepository outbox;

    public OrderService(OrderRepository orders, OutboxRepository outbox) {
        this.orders = orders;
        this.outbox = outbox;
    }

    @Transactional
    public void placeOrder(Order order) {
        orders.save(order);
        outbox.save(new OutboxMessage("orders", order.getId()));   // same transaction as the order
        if (order.getAmount() > 1000) {
            throw new IllegalStateException("Amount too high!");   // rolls back the order AND the outbox row
        }
    }
}
