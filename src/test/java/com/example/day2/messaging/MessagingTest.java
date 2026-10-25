package com.example.day2.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.day2.shop.Order;
import com.example.day2.shop.OrderRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Topic 4: messages and transactions. There is NO @Transactional on this class.
 * After every placeOrder() the tests call publisher.publishPending(): that call does nothing
 * until you have written it in step 4, so the same tests describe every stage of the exercise.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1_2, step2, step3, step4...
class MessagingTest {

    @Autowired
    OrderService service;

    @Autowired
    OrderRepository orders;

    @Autowired
    OutboxRepository outbox;

    @Autowired
    OutboxPublisher publisher;

    @Autowired
    FakeMessageBroker broker;

    @BeforeEach
    void clean() {
        orders.deleteAllInBatch();
        outbox.deleteAllInBatch();
        broker.clear();
        System.out.println("[messaging] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        System.out.println("[messaging] after the test: orders = " + orders.count()
                + ", outbox rows = " + outbox.count() + " (unsent: " + outbox.findUnsent().size() + ")"
                + ", messages sent = " + broker.sentMessages());
    }

    @Test
    void step1_2_aRollbackMustNotSendAMessage() {
        assertThrows(Exception.class, () -> service.placeOrder(new Order(2000)));
        publisher.publishPending();

        assertThat(orders.count()).as("the order was rolled back").isZero();
        assertThat(broker.sentMessages()).as("no message may leave for an order that does not exist").isEmpty();
    }

    @Test
    void step2_aCommitSendsTheMessage() {
        service.placeOrder(new Order(50));
        publisher.publishPending();

        Long id = orders.findAll().get(0).getId();
        assertThat(broker.sentMessages()).containsExactly("orders:" + id);
    }

    @Test
    void step3_brokerDown_theOrderExistsWithoutAMessage() {
        // No code of its own: this test turns green in step 2. After step 4 it stays green for another
        // reason: the message now waits in the outbox, and this test does not call publishPending().
        broker.setFailing(true);
        try {
            service.placeOrder(new Order(50));
            System.out.println("[messaging] step 3: placeOrder() returned normally");
        } catch (RuntimeException e) {
            System.out.println("[messaging] step 3: the caller saw: " + e.getClass().getSimpleName());
        }

        assertThat(orders.count()).as("the order was committed").isEqualTo(1);
        assertThat(broker.sentMessages()).isEmpty();
    }

    @Test
    void step4_aRollbackLeavesNothingInTheOutbox() {
        assertThrows(Exception.class, () -> service.placeOrder(new Order(2000)));

        assertThat(outbox.count()).isZero();
    }

    @Test
    void step4_orderAndOutboxMessageAreSavedTogether() {
        service.placeOrder(new Order(50));

        assertThat(orders.count()).isEqualTo(1);
        assertThat(outbox.count()).as("the message is saved in the same transaction").isEqualTo(1);
    }

    @Test
    void step4_publisherKeepsTheMessageWhileTheBrokerIsDown() {
        broker.setFailing(true);
        try {
            service.placeOrder(new Order(50));
        } catch (RuntimeException ignored) {
            // the caller may or may not see an exception, depending on how far you are
        }
        publisher.publishPending();

        assertThat(outbox.findUnsent()).as("the message must stay in the outbox").hasSize(1);
        assertThat(broker.sentMessages()).isEmpty();

        broker.setFailing(false);
        publisher.publishPending();

        assertThat(outbox.findUnsent()).as("nothing is left to send").isEmpty();
        assertThat(broker.sentMessages()).hasSize(1);
    }
}
