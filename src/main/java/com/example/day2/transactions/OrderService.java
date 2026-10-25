package com.example.day2.transactions;

import com.example.day2.shop.Order;
import com.example.day2.shop.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final AuditService audit;

    public OrderService(OrderRepository orders, AuditService audit) {
        this.orders = orders;
        this.audit = audit;
    }

    @Transactional
    public void placeOrder(Order order) {
        audit.log("placing order");
        orders.save(order);
        if (order.getAmount() > 1000) {
            throw new IllegalStateException("Amount too high!");
        }
    }
}
