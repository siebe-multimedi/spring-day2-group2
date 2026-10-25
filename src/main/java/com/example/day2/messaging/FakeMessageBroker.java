package com.example.day2.messaging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

/**
 * A fake broker: it records what leaves the application, so that the exercises are about
 * transactions and timing and do not need a real RabbitMQ.
 */
@Component
public class FakeMessageBroker implements MessageBroker {

    private final List<String> sent = new CopyOnWriteArrayList<>();
    private volatile boolean failing;

    @Override
    public void send(String queue, Long orderId) {
        if (failing) {
            throw new BrokerUnavailableException("The broker is down");
        }
        sent.add(queue + ":" + orderId);
        System.out.println("[messaging] message sent to " + queue + ": order " + orderId);
    }

    @Override
    public List<String> sentMessages() {
        return List.copyOf(sent);
    }

    @Override
    public void setFailing(boolean failing) {
        this.failing = failing;
    }

    /** Used by the tests to start every test with an empty, working broker. */
    public void clear() {
        sent.clear();
        failing = false;
    }
}
