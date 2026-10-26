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

    // Step 2: a checked exception does not roll back by default, so we ask for it with rollbackFor.
    @Transactional(rollbackFor = OrderRejectedException.class)
    public void placeOrder(Order order) throws OrderRejectedException {
        // Step 3: a failing audit must not stop the order. This only works because
        // AuditService.log() has REQUIRES_NEW: otherwise the failed call would mark OUR transaction
        // as rollback-only, and the commit below would fail with an UnexpectedRollbackException.
        try {
            audit.log("placing order");
        } catch (RuntimeException e) {
            System.out.println("[transactions] the audit failed, the order goes on: " + e.getMessage());
        }
        orders.save(order);
        if (order.getAmount() > 1000) {
            throw new OrderRejectedException("Amount too high!");
        }
    }
}
