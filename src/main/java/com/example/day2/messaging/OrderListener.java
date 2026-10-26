package com.example.day2.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Bonus: an idempotent consumer. */
@Component
public class OrderListener {

    private final Warehouse warehouse;
    private final ProcessedMessageRepository processed;

    public OrderListener(Warehouse warehouse, ProcessedMessageRepository processed) {
        this.warehouse = warehouse;
        this.processed = processed;
    }

    /** In a real application this is a message listener (with Spring AMQP: @RabbitListener). */
    @Transactional
    public void handle(String messageId, Long orderId) {
        if (processed.existsById(messageId)) {
            System.out.println("[messaging] duplicate message " + messageId + " ignored");
            return;
        }
        warehouse.reserve(orderId);
        // The id is the primary key: if two copies arrive at the same moment, the second insert fails.
        processed.save(new ProcessedMessage(messageId));
    }
}
