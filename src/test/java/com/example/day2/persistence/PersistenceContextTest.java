package com.example.day2.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Topic 1: the persistence context. There is NO @Transactional on this class: every call to the
 * service or the repository runs in its own transaction.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)   // the steps run in order: step1, step2, step3
class PersistenceContextTest {

    @Autowired
    CustomerService service;

    @Autowired
    CustomerRepository repository;

    private Long annId;   // "customer 1"
    private Long bobId;   // "customer 2"

    @BeforeEach
    void insertTwoCustomers() {
        repository.deleteAllInBatch();
        annId = repository.save(new Customer("Ann", "ann@example.org")).getId();
        bobId = repository.save(new Customer("Bob", "bob@example.org")).getId();
        System.out.println("[persistence] ---- setup done: the test starts here ----");
    }

    @AfterEach
    void report() {
        repository.findAll().forEach(customer -> System.out.println(
                "[persistence] after the test: " + customer.getName() + " <" + customer.getEmail() + ">"));
    }

    @Test
    void step1_dirtyChecking_theNewNameIsStoredWithoutSave() {
        service.rename(annId, "Anna");

        Customer reloaded = repository.findById(annId).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Anna");
    }

    @Test
    void step2_detached_theChangeIsNotStored() {
        Customer detached = service.load(annId);
        detached.setName("Changed");   // nobody is watching this object

        Customer reloaded = service.load(annId);
        System.out.println("[persistence] step 2: after the change, load() returns the name: " + reloaded.getName());
        assertThat(reloaded.getName()).isEqualTo("Ann");
    }

    @Test
    void step3_duplicateEmail_whereDoesTheExceptionAppear() {
        RuntimeException outside = null;
        try {
            service.changeEmail(bobId, "ann@example.org");   // the email of Ann: it must be unique
        } catch (RuntimeException e) {
            outside = e;
            System.out.println("[persistence] step 3: an exception surfaced OUTSIDE changeEmail(): "
                    + e.getClass().getSimpleName());
        }

        assertThat(outside != null || service.lastCallCaughtInside())
                .as("step 3: the duplicate email must be rejected somewhere")
                .isTrue();
        // Whichever way it was reported: the duplicate email must never be stored.
        assertThat(repository.findById(bobId).orElseThrow().getEmail()).isEqualTo("bob@example.org");
    }
}
