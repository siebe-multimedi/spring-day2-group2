package com.example.day2.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Bonus of topic 4: an idempotent consumer. */
@SpringBootTest
class MessagingBonusTest {

    @Autowired
    OrderListener listener;

    @Autowired
    Warehouse warehouse;

    @Autowired
    ProcessedMessageRepository processed;

    @BeforeEach
    void clean() {
        processed.deleteAllInBatch();
        warehouse.clear();
        System.out.println("[messaging] ---- setup done: the test starts here ----");
    }

    @Test
    void bonus_theSameMessageTwice_isHandledOnce() {
        listener.handle("message-1", 7L);
        listener.handle("message-1", 7L);   // a duplicate: the same message id

        assertThat(warehouse.reservations()).as("the stock must be reserved only once").hasSize(1);
    }
}
