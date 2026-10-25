package com.example.day2.transactions;

/** A checked exception: Spring does NOT roll back on it by default (step 2). */
public class OrderRejectedException extends Exception {

    public OrderRejectedException(String message) {
        super(message);
    }
}
