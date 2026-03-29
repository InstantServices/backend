package com.instantservices.backend.service;



import com.instantservices.backend.model.*;
import com.instantservices.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;
    private final PaymentService paymentService;
    private final TrustScoreService trustScoreService;
    private final PaymentRepository paymentRepository;

    public DisputeService(DisputeRepository disputeRepository,
                          TaskRepository taskRepository,
                          AppUserRepository userRepository,
                          PaymentService paymentService,
                          TrustScoreService trustScoreService, PaymentRepository paymentRepository) {
        this.disputeRepository = disputeRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.trustScoreService = trustScoreService;
        this.paymentRepository = paymentRepository;
    }

    // STEP 3 — Raise Dispute
    @Transactional
    public Dispute raiseDispute(Long taskId, String email, String reason, String description) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = paymentRepository
                .findTopByTaskIdOrderByCreatedAtDesc(taskId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // IMPORTANT: Preserve where money is
        if (payment.getStatus() == PaymentStatus.HELD) {
            payment.setStatus(PaymentStatus.DISPUTED_HELD);
        }
        else if (payment.getStatus() == PaymentStatus.RELEASED) {
            payment.setStatus(PaymentStatus.DISPUTED_RELEASED);
        }

        paymentRepository.save(payment);

        Dispute dispute = new Dispute();
        dispute.setTaskId(taskId);
        dispute.setRaisedByUserId(user.getId());
        dispute.setReason(reason);
        dispute.setDescription(description);
        dispute.setStatus("OPEN");
        dispute.setCreatedAt(Instant.now());

        disputeRepository.save(dispute);

        user.setDisputes(user.getDisputes() + 1);
        trustScoreService.updateTrustScore(user);

        return dispute;
    }

    // STEP 4 — Admin Decision
    @Transactional
    public String resolveDispute(Long disputeId, String decision) {


        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        if (dispute.getStatus().equals("RESOLVED")) {
            throw new RuntimeException("Dispute already resolved");
        }

        Task task = taskRepository.findById(dispute.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        System.out.println("=== ADMIN RESOLVING DISPUTE ===");
        System.out.println("Dispute ID: " + disputeId);



        if (decision.equals("REFUND_POSTER")) {
            paymentService.refundToPoster(task.getId());
            dispute.setAdminDecision("REFUND_POSTER");
            task.setStatus(TaskStatus.REFUNDED);
        }
        else if (decision.equals("PAY_DOER")) {
            paymentService.releaseFunds(task.getId());
            dispute.setAdminDecision("PAY_DOER");
            task.setStatus(TaskStatus.COMPLETED);
        }

        System.out.println("Task ID: " + dispute.getTaskId());
        dispute.setStatus("RESOLVED");
        System.out.println("Decision: " + decision);

        disputeRepository.save(dispute);
        taskRepository.save(task);

        return "Dispute resolved: " + decision;
    }
}