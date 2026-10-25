package com.example.day2.transactions;

import org.springframework.stereotype.Service;
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

    @Transactional
    public void log(String message) {
        audits.save(new AuditEntry(message));
        if (failing) {
            throw new IllegalStateException("Audit store unavailable");
        }
    }

    /** Bonus: this method should only ever run inside an existing transaction. */
    @Transactional
    public void logInExistingTransaction(String message) {
        audits.save(new AuditEntry(message));
    }
}
