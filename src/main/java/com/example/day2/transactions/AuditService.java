package com.example.day2.transactions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository audits;

    // Lets the tests simulate an audit store that is down (step 3).
    private volatile boolean failing;

    public AuditService(AuditRepository audits) {
        this.audits = audits;
    }

    public void setFailing(boolean failing) {
        this.failing = failing;
    }

    /**
     * Step 3 and 4: REQUIRES_NEW. The audit entry commits (or fails) in a transaction of its own,
     * so it survives a rollback of the order, and a failing audit does not doom the order.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String message) {
        audits.save(new AuditEntry(message));
        if (failing) {
            throw new IllegalStateException("Audit store unavailable");
        }
    }

    /** Bonus: MANDATORY. Without an existing transaction this method refuses to run. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void logInExistingTransaction(String message) {
        audits.save(new AuditEntry(message));
    }
}
