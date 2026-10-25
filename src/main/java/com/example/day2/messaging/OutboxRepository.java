package com.example.day2.messaging;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    @Query("select m from OutboxMessage m where m.sent = false order by m.id")
    List<OutboxMessage> findUnsent();

    /** Marks one row as sent, in its own short transaction. */
    @Modifying
    @Transactional
    @Query("update OutboxMessage m set m.sent = true where m.id = :id")
    void markSent(@Param("id") Long id);
}
