package com.example.day2.messaging;

import org.springframework.stereotype.Component;

/** Step 4: sends the messages that are waiting in the outbox. */
@Component
public class OutboxPublisher {

    private final OutboxRepository outbox;
    private final MessageBroker broker;

    public OutboxPublisher(OutboxRepository outbox, MessageBroker broker) {
        this.outbox = outbox;
        this.broker = broker;
    }

    /** In a real application this method runs on a schedule (@Scheduled). Here the tests call it. */
    public void publishPending() {
        // No @Transactional on this method: a remote call does not belong inside a transaction.
        for (OutboxMessage message : outbox.findUnsent()) {
            try {
                broker.send(message.getQueue(), message.getPayload());
            } catch (BrokerUnavailableException e) {
                System.out.println("[messaging] the broker is down: the message stays in the outbox");
                return;
            }
            outbox.markSent(message.getId());   // its own, short transaction
        }
    }
}
