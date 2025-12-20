package com.instantservices.backend.service;

import com.instantservices.backend.model.*;
import com.instantservices.backend.repository.DeliveryProofRepository;
import com.instantservices.backend.repository.PaymentRepository;
import com.instantservices.backend.repository.TaskRepository;
import com.instantservices.backend.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
public class DeliveryService {

    private final DeliveryProofRepository dpRepo;
    private final TaskRepository taskRepo;
    private final AppUserRepository userRepo;
    private final PaymentRepository paymentRepo;
    private final PaymentService paymentService;

    public DeliveryService(DeliveryProofRepository dpRepo,
                           TaskRepository taskRepo,
                           AppUserRepository userRepo,
                           PaymentRepository paymentRepo,
                           PaymentService paymentService) {
        this.dpRepo = dpRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.paymentRepo = paymentRepo;
        this.paymentService = paymentService;
    }

    // Generate and store OTP for a task delivery. Returns OTP in response (dev only).
    @Transactional
    public String generateOtpForDelivery(Long taskId, String doerEmail) {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        if (task.getAcceptedBy() == null || !task.getAcceptedBy().getEmail().equals(doerEmail)) {
            throw new RuntimeException("Only accepted doer can mark as delivered");
        }
        // create 6-digit OTP
        Random r = new Random();
        int otp = 100000 + r.nextInt(900000);
        String otpStr = String.valueOf(otp);

        // hash OTP with bcrypt
        String hash = BCrypt.hashpw(otpStr, BCrypt.gensalt());

        DeliveryProof dp = new DeliveryProof();
        dp.setTaskId(taskId);
        dp.setDoerId(task.getAcceptedBy().getId());
        dp.setType("OTP");
        dp.setOtpHash(hash);
        dp.setOtpExpiresAt(Instant.now().plusSeconds(15 * 60)); // 15 minutes
        dp.setCreatedAt(Instant.now());
        dpRepo.save(dp);

        // In production: send OTP via SMS to task.poster.phone. For dev, return OTP.
        return otpStr;
    }

    // Poster confirms OTP — this triggers payment release and task completion.
    @Transactional
    public void confirmDeliveryWithOtp(Long taskId, String posterEmail, String otp) {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getPoster().getEmail().equals(posterEmail)) {
            throw new RuntimeException("Only poster can confirm delivery");
        }

        DeliveryProof dp = dpRepo.findByTaskIdAndType(taskId, "OTP")
                .orElseThrow(() -> new RuntimeException("No delivery OTP found for this task"));

        if (dp.getOtpExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("OTP expired");
        }

        boolean ok = BCrypt.checkpw(otp, dp.getOtpHash());
        if (!ok) {
            throw new RuntimeException("Invalid OTP");
        }

        // mark proof verified
        dp.setVerifiedBy(task.getPoster().getId());
        dp.setVerifiedAt(Instant.now());
        dpRepo.save(dp);

        // release payment -- use repository helper that returns the latest HELD payment
        Payment payment = paymentRepo.findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD)
                .orElseThrow(() -> new RuntimeException("Held payment not found for task"));
        paymentService.releaseFunds(taskId); // will update DB via service
        // (optional) you can refresh or re-fetch the payment record if you need the final state

        // update task status and user's metrics
        task.setStatus(TaskStatus.COMPLETED);
        taskRepo.save(task);

        // update doer earnings & tasksCompleted
        AppUser doer = task.getAcceptedBy();
        if (doer != null) {
            doer.setTasksCompleted((doer.getTasksCompleted() == null ? 0 : doer.getTasksCompleted()) + 1);
            // increment doer total earnings by commission (or whatever business rule)
            double inc = (task.getCommission() == null ? 0.0 : task.getCommission());
            doer.setTotalEarnings((doer.getTotalEarnings() == null ? 0.0 : doer.getTotalEarnings()) + inc);
            userRepo.save(doer);
        }
    }
}
