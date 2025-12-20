package com.instantservices.backend.repository;

import com.instantservices.backend.model.Payment;
import com.instantservices.backend.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Payments for a task (historical). Use when you want to inspect all records.
     */
    List<Payment> findByTaskId(Long taskId);

    /**
     * Get the most recent payment for a task with the given status (null if not found).
     * Implemented by Spring Data using method name resolution.
     */
    Optional<Payment> findTopByTaskIdAndStatusOrderByCreatedAtDesc(Long taskId, PaymentStatus status);

    /**
     * Convenience when you only care if any payment exists with this task and status.
     */
    List<Payment> findByTaskIdAndStatus(Long taskId, PaymentStatus status);
}
