package com.example.day2.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    // Makes step 3 observable in the test: did changeEmail() catch the exception itself?
    private volatile boolean lastCallCaughtInside;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    /** Step 1: change the name of a customer, WITHOUT calling save(). */
    @Transactional
    public void rename(Long id, String newName) {
        // TODO step 1: load the customer and change its name. Do NOT call save().
    }

    /** Step 2: this method has no @Transactional, and the test that calls it has none either. */
    public Customer load(Long id) {
        return repository.findById(id).orElseThrow();
    }

    /** Step 3: give a customer a new email, inside the try block. */
    @Transactional
    public void changeEmail(Long id, String email) {
        lastCallCaughtInside = false;
        try {
            // TODO step 3: load the customer and give it the new email
        } catch (DataIntegrityViolationException e) {
            lastCallCaughtInside = true;
            System.out.println("[persistence] step 3: the exception was caught INSIDE changeEmail()");
        }
    }

    public boolean lastCallCaughtInside() {
        return lastCallCaughtInside;
    }
}
