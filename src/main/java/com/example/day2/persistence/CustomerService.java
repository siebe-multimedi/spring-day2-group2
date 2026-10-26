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

    /** Step 1: dirty checking. The customer is managed, so the change is written at commit. No save() needed. */
    @Transactional
    public void rename(Long id, String newName) {
        Customer customer = repository.findById(id).orElseThrow();
        customer.setName(newName);
    }

    /** Step 2: no @Transactional: the entity that is returned is detached. */
    public Customer load(Long id) {
        return repository.findById(id).orElseThrow();
    }

    /** Step 3: give a customer a new email, inside the try block. */
    @Transactional
    public void changeEmail(Long id, String email) {
        lastCallCaughtInside = false;
        try {
            Customer customer = repository.findById(id).orElseThrow();
            customer.setEmail(email);
            // First run: without the next line the UPDATE only runs at commit, outside this try block.
            // Second run: with the flush the UPDATE runs here, and the catch block is reached.
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            lastCallCaughtInside = true;
            System.out.println("[persistence] step 3: the exception was caught INSIDE changeEmail()");
        }
    }

    public boolean lastCallCaughtInside() {
        return lastCallCaughtInside;
    }
}
