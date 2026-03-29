package com.instantservices.backend.service;


import com.instantservices.backend.model.*;
import com.instantservices.backend.repository.PaymentRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AutoReleaseService {

    private final TaskRepository taskRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final TrustScoreService trustScoreService;

    public AutoReleaseService(TaskRepository taskRepository,
                              PaymentService paymentService,
                              PaymentRepository paymentRepository, TrustScoreService trustScoreService) {
        this.taskRepository = taskRepository;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.trustScoreService = trustScoreService;
    }

    // Runs every 1 hour

    @Scheduled(fixedRate = 3600000)
    public void autoReleasePayments() {

        Instant now = Instant.now();
        Instant cutoff = now.minus(2, ChronoUnit.MINUTES);

        List<Task> tasks = taskRepository.findByStatus(TaskStatus.ARRIVED);
        for (Task task : tasks) {
            if (task.getArrivedAt() != null && task.getArrivedAt().isBefore(cutoff)) {
                try {

                    Payment payment = paymentRepository
                            .findTopByTaskIdOrderByCreatedAtDesc(task.getId())
                            .orElse(null);

                    if (payment == null) continue;

                    // IMPORTANT CHECK
                    if (payment.getStatus() != PaymentStatus.HELD) {
                        continue;
                    }

                    paymentService.releaseFunds(task.getId());
                    task.setStatus(TaskStatus.COMPLETED);
                    taskRepository.save(task);

                    System.out.println("Auto released payment for task: " + task.getId());

                } catch (Exception e) {
                    System.out.println("Auto release failed for task: " + task.getId());
                }
            }
        }


    }
}