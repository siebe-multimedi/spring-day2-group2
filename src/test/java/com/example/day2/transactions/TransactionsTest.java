package com.example.day2.transactions;

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

/** Topic 2: transactions. There is NO @Transactional on this class. */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, ...
class TransactionsTest {

    @Autowired
    OrderService service;

    @Autowired
    AuditService audit;

    @Autowired
    OrderRepository orders;

    @Autowired
    AuditRepository audits;

    @BeforeEach
    void clean() {
        orders.deleteAllInBatch();
        audits.deleteAllInBatch();
        audit.setFailing(false);
        System.out.println("[transactions] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        audit.setFailing(false);
        System.out.println("[transactions] after the test: orders = " + orders.count()
                + ", audit entries = " + audits.count());
    }

    @Test
    void step1_observe_requiredJoinsTheTransaction() {
        assertThrows(Exception.class, () -> service.placeOrder(new Order(2000)));

        System.out.println("[transactions] step 1: orders = " + orders.count() + ", audit entries = " + audits.count());
        assertThat(orders.count()).isZero();
    }

    @Test
    void step2_checkedException_theTransactionMustRollBack() {
        assertThrows(OrderRejectedException.class, () -> service.placeOrder(new Order(2000)));

        assertThat(orders.count()).as("the rejected order must not be saved").isZero();
    }

    @Test
    void step3_failingAudit_theOrderIsStillSaved() throws Exception {
        audit.setFailing(true);

        service.placeOrder(new Order(50));   // must not throw

        assertThat(orders.count()).isEqualTo(1);
    }

    @Test
    void step4_requiresNew_theAuditSurvivesTheRollback() {
        // No code of its own: this test turns green with the REQUIRES_NEW of step 3.
        assertThrows(Exception.class, () -> service.placeOrder(new Order(2000)));

        assertThat(orders.count()).as("the order was rolled back").isZero();
        assertThat(audits.count()).as("the audit entry must survive the rollback").isEqualTo(1);
    }
}
