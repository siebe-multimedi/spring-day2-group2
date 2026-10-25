package com.example.day2.messaging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

/** The system that listens to the messages and reserves stock (bonus). */
@Component
public class Warehouse {

    private final List<Long> reservations = new CopyOnWriteArrayList<>();

    public void reserve(Long orderId) {
        reservations.add(orderId);
        System.out.println("[messaging] stock reserved for order " + orderId);
    }

    public List<Long> reservations() {
        return List.copyOf(reservations);
    }

    public void clear() {
        reservations.clear();
    }
}
