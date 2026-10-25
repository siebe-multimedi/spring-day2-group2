package com.example.day2.messaging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A row of the outbox table (step 4). */
@Entity
@Table(name = "outbox_messages")
public class OutboxMessage {

    @Id
    @GeneratedValue
    private Long id;

    private String queue;

    private Long payload;

    private boolean sent;

    protected OutboxMessage() {
    }

    public OutboxMessage(String queue, Long payload) {
        this.queue = queue;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public String getQueue() {
        return queue;
    }

    public Long getPayload() {
        return payload;
    }

    public boolean isSent() {
        return sent;
    }
}
