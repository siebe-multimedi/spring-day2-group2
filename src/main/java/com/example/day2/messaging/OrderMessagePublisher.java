package com.example.day2.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Step 2 (intermediate state): send the message AFTER the commit.
 * In step 2 OrderService publishes an OrderPlaced event inside the transaction:
 *
 *     orders.save(order);
 *     events.publishEvent(new OrderPlaced(order.getId()));
 *
 * In step 4 this is replaced by the outbox, so nothing publishes the event any more.
 */
@Component
public class OrderMessagePublisher {

    private final MessageBroker broker;

    public OrderMessagePublisher(MessageBroker broker) {
        this.broker = broker;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPlaced event) {
        broker.send("orders", event.orderId());
    }
}
