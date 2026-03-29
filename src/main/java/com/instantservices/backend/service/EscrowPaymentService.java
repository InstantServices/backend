package com.instantservices.backend.service;

import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.model.Payment;
import com.instantservices.backend.model.PaymentStatus;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class EscrowPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppUserRepository userRepository;

    public EscrowPaymentService(PaymentRepository paymentRepository,
                                AppUserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Payment holdFunds(Long taskId, Long posterId, Long doerId, Double amount) {

        AppUser poster = userRepository.findById(posterId)
                .orElseThrow(() -> new RuntimeException("Poster not found"));

        if (poster.getWalletBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from poster wallet
        poster.setWalletBalance(poster.getWalletBalance() - amount);
        userRepository.save(poster);

        Payment payment = new Payment();
        payment.setTaskId(taskId);
        payment.setPosterId(posterId);
        payment.setDoerId(doerId);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.HELD);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment releaseFunds(Long taskId) {
        System.out.println("=== REFUND STARTED ===");
        System.out.println("Task ID: " + taskId);

        Payment payment = paymentRepository
                .findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD)
                .orElseThrow(() -> new RuntimeException("Held payment not found"));

        AppUser doer = userRepository.findById(payment.getDoerId())
                .orElseThrow(() -> new RuntimeException("Doer not found"));

        doer.setWalletBalance(doer.getWalletBalance() + payment.getAmount());
        userRepository.save(doer);

        payment.setStatus(PaymentStatus.RELEASED);
        payment.setUpdatedAt(Instant.now());

        return paymentRepository.save(payment);
    }
    @Override
    @Transactional
    public Payment refundToPoster(Long taskId) {

        Payment payment = paymentRepository
                .findTopByTaskIdAndStatusInOrderByCreatedAtDesc(
                        taskId,
                        List.of(
                                PaymentStatus.HELD,
                                PaymentStatus.RELEASED,
                                PaymentStatus.DISPUTED_HELD,
                                PaymentStatus.DISPUTED_RELEASED
                        )
                )
                .orElseThrow(() -> new RuntimeException("Active payment not found"));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Payment already refunded");
        }

        AppUser poster = userRepository.findById(payment.getPosterId())
                .orElseThrow(() -> new RuntimeException("Poster not found"));

        AppUser doer = userRepository.findById(payment.getDoerId())
                .orElseThrow(() -> new RuntimeException("Doer not found"));

        Double amount = payment.getAmount();

        System.out.println("=== REFUND DEBUG ===");
        System.out.println("Payment Status: " + payment.getStatus());
        System.out.println("Poster Before: " + poster.getWalletBalance());
        System.out.println("Doer Before: " + doer.getWalletBalance());

        // Money still in escrow
        if (payment.getStatus() == PaymentStatus.HELD ||
                payment.getStatus() == PaymentStatus.DISPUTED_HELD) {

            poster.setWalletBalance(poster.getWalletBalance() + amount);
        }

        // Money already given to doer
        else if (payment.getStatus() == PaymentStatus.RELEASED ||
                payment.getStatus() == PaymentStatus.DISPUTED_RELEASED) {

            doer.setWalletBalance(doer.getWalletBalance() - amount);
            poster.setWalletBalance(poster.getWalletBalance() + amount);

            // reduce earnings also
            doer.setTotalEarnings(doer.getTotalEarnings() - amount);
            userRepository.save(doer);
        }

        userRepository.save(poster);

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(Instant.now());

        System.out.println("Poster After: " + poster.getWalletBalance());
        System.out.println("Doer After: " + doer.getWalletBalance());
        System.out.println("====================");

        return paymentRepository.save(payment);
    }
}