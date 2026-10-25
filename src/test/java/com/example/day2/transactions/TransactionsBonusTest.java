package com.example.day2.transactions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.IllegalTransactionStateException;

/** Bonus of topic 2: propagation MANDATORY. */
@SpringBootTest
class TransactionsBonusTest {

    @Autowired
    AuditService audit;

    @Test
    void bonus_mandatory_withoutATransactionTheCallIsRefused() {
        assertThrows(IllegalTransactionStateException.class,
                () -> audit.logInExistingTransaction("no transaction here"));
    }
}
