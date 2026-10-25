package com.example.day2.messaging;

import org.springframework.stereotype.Component;

/** Step 2: send the message AFTER the commit. */
@Component
public class OrderMessagePublisher {

    private final MessageBroker broker;

    public OrderMessagePublisher(MessageBroker broker) {
        this.broker = broker;
    }

    // TODO step 2: write a method that receives an OrderPlaced event and sends the message,
    //              but only after the transaction has committed.
    //              (hint: @TransactionalEventListener)
    //              OrderService must publish the event with an ApplicationEventPublisher,
    //              instead of calling the broker itself.
}
