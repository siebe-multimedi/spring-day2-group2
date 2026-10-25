package com.example.day2.messaging;

import java.util.List;

public interface MessageBroker {

    void send(String queue, Long orderId);

    /** What has left the application so far. */
    List<String> sentMessages();

    /** Simulates a broker that is down (true) or up again (false). */
    void setFailing(boolean failing);
}
