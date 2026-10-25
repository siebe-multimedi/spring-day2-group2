package com.example.day2.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An order, shared by the transactions exercise and the messaging exercise.
 * "order" is a reserved word in SQL, which is why the table is called "orders".
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private double amount;

    protected Order() {
    }

    public Order(double amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }
}
