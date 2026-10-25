package com.example.day2.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Bonus of topic 1: make the change to a detached entity reach the database. */
@SpringBootTest
class PersistenceBonusTest {

    @Autowired
    CustomerService service;

    @Autowired
    CustomerRepository repository;

    private Long annId;

    @BeforeEach
    void insertACustomer() {
        repository.deleteAllInBatch();
        annId = repository.save(new Customer("Ann", "ann@example.org")).getId();
        System.out.println("[persistence] ---- setup done: the test starts here ----");
    }

    @Test
    void bonus_saveOnTheDetachedEntity_mergesTheChange() {
        Customer detached = service.load(annId);
        detached.setName("Changed");

        // TODO bonus: make this change reach the database, with ONE repository call.
        //             Which SQL do you see in the console?

        assertThat(service.load(annId).getName()).isEqualTo("Changed");
    }
}
