package com.example.day2.messaging;

import com.example.day2.shop.Order;
import com.example.day2.shop.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final MessageBroker broker;

    public OrderService(OrderRepository orders, MessageBroker broker) {
        this.orders = orders;
        this.broker = broker;
    }

    @Transactional
    public void placeOrder(Order order) {
        orders.save(order);
        broker.send("orders", order.getId());   // the problem: sent before the commit
        if (order.getAmount() > 1000) {
            throw new IllegalStateException("Amount too high!");
        }
    }
}
