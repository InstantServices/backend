package com.instantservices.backend.service;

import com.instantservices.backend.model.Payment;
import com.instantservices.backend.model.PaymentStatus;
import com.instantservices.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class MockPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    public MockPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment holdFunds(Long taskId, Long posterId, Long doerId, Double amount) {

        // If there's already a held payment for this task, return it (idempotent).
        var existingHeld = paymentRepository.findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD);
        if (existingHeld.isPresent()) {
            return existingHeld.get();
        }

        // Otherwise create a new payment hold (mock)
        Payment p = new Payment();
        p.setTaskId(taskId);
        p.setPosterId(posterId);
        p.setDoerId(doerId);
        p.setAmount(amount);

        // mock gateway ID and hold id
        p.setGatewayTxnId("mock-" + UUID.randomUUID());
        p.setHoldId("mock-hold-" + UUID.randomUUID());

        // Use enum PaymentStatus
        p.setStatus(PaymentStatus.HELD);
        p.setGatewayStatus(PaymentStatus.HELD.name());

        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());

        return paymentRepository.save(p);
    }

    @Override
    @Transactional
    public Payment releaseFunds(Long taskId) {
        // Find the latest HELD payment for this task
        var heldOpt = paymentRepository.findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD);
        if (heldOpt.isEmpty()) {
            throw new RuntimeException("Held payment not found for task " + taskId);
        }

        Payment p = heldOpt.get();

        // Simulate capture/release
        p.setStatus(PaymentStatus.RELEASED);
        p.setGatewayStatus(PaymentStatus.RELEASED.name());
        p.setUpdatedAt(Instant.now());

        return paymentRepository.save(p);
    }
}
