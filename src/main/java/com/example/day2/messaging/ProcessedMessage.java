package com.example.day2.messaging;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** The id of a message that was handled before (bonus: idempotent consumer). */
@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {

    @Id
    private String id;

    protected ProcessedMessage() {
    }

    public ProcessedMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
