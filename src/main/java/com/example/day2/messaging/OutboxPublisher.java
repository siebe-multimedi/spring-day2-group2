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
        // TODO step 4: send every unsent OutboxMessage (outbox.findUnsent()),
        //              and mark each one as sent (outbox.markSent(id)) after it was sent.
        //              If the broker is down, it throws a BrokerUnavailableException:
        //              catch it, and leave the message in the outbox.
    }
}
