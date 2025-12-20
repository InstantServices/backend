package com.instantservices.backend.service;


import com.instantservices.backend.dto.*;
import com.instantservices.backend.model.*;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.DeliveryProofRepository;
import com.instantservices.backend.repository.PaymentRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Random;

import com.instantservices.backend.service.PaymentService;


import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;
    private final UserProfileService userProfileService;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final PasswordEncoder passwordEncoder;
    private final DeliveryProofRepository dpRepo;


    public TaskService(TaskRepository taskRepository,
                       AppUserRepository userRepository,
                       UserProfileService userProfileService, PaymentRepository paymentRepository, PaymentService paymentService, PasswordEncoder passwordEncoder, DeliveryProofRepository dpRepo) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.passwordEncoder = passwordEncoder;
        this.dpRepo = dpRepo;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest req) {
        // Basic validation
        if (req.getCommission() == null || req.getCommission() < 0) {
            throw new IllegalArgumentException("Commission must be provided and >= 0");
        }
        if (req.getOfferedPrice() == null || req.getOfferedPrice() < 0) {
            throw new IllegalArgumentException("Offered price must be provided and >= 0");
        }
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        // Get current user (poster)
        AppUser poster = userProfileService.getCurrentAppUser()
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setCategory(req.getCategory());
        t.setOfferedPrice(req.getOfferedPrice());
        t.setCommission(req.getCommission());
        t.setCity(req.getCity());
        t.setLatitude(req.getLatitude());
        t.setLongitude(req.getLongitude());
        t.setPoster(poster);
        t.setStatus(TaskStatus.OPEN);

        Task saved = taskRepository.save(t);

        // update poster metrics
        poster.setTasksPosted((poster.getTasksPosted() == null ? 0 : poster.getTasksPosted()) + 1);
        userRepository.save(poster);

        return toResponse(saved);
    }

    public Page<TaskResponse> listOpenTasks(int page, int size, String category, String city) {
        PageRequest pr = PageRequest.of(page, size);
        Page<Task> pageRes;
        if (category != null && city != null) {
            pageRes = taskRepository.findByCategoryAndStatus(category, TaskStatus.OPEN, pr);
            // city filter not combined in repository method, use fallback
            pageRes = pageRes.map(t -> t).map(p -> p); // noop
        } else if (category != null) {
            pageRes = taskRepository.findByCategoryAndStatus(category, TaskStatus.OPEN, pr);
        } else if (city != null) {
            pageRes = taskRepository.findByCityAndStatus(city, TaskStatus.OPEN, pr);
        } else {
            pageRes = taskRepository.findByStatus(TaskStatus.OPEN, pr);
        }

        Page<TaskResponse> resp = pageRes.map(this::toResponse);
        return resp;
    }

    public TaskResponse getTask(Long id) {
        Task t = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return toResponse(t);
    }
    @Transactional
    public DeliveryResponse markDelivered(Long taskId, DeliveryProofRequest req, String email) throws IOException {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAcceptedBy().getEmail().equals(email)) {
            throw new RuntimeException("Only assigned doer can deliver.");
        }

        // Create DeliveryProof entry
        DeliveryProof dp = new DeliveryProof();
        dp.setTaskId(taskId);
        dp.setDoerId(task.getAcceptedBy().getId());
        dp.setCreatedAt(Instant.now());

        // ============================
        // 1) PHOTO PROOF (optional)
        // ============================
        // Ensure uploads directory exists
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        if (req.getPhoto() != null && !req.getPhoto().isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + req.getPhoto().getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.copy(req.getPhoto().getInputStream(), path);
            dp.setPhotoUrl("/uploads/" + fileName);
            dp.setType("PHOTO");
        }

        // ============================
        // 2) OTP PROOF (optional)
        // ============================
        String otp = null;
        if (req.isGenerateOtp()) {
            otp = String.valueOf(100000 + new Random().nextInt(900000));
            dp.setOtpHash(passwordEncoder.encode(otp));
            dp.setOtpExpiresAt(Instant.now().plusSeconds(900));
            dp.setType(dp.getType() == null ? "OTP" : "PHOTO+OTP");
        }

        dpRepo.save(dp);

        // Update task status
        task.setStatus(TaskStatus.DELIVERED);
        taskRepository.save(task);

        // Response
        DeliveryResponse resp = new DeliveryResponse();
        resp.setMessage("Delivery proof submitted");
        resp.setOtp(otp); // for dev only

        return resp;
    }

    @Transactional
    public ConfirmResponse confirmDelivery(Long taskId, String otp, String posterEmail) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getPoster().getEmail().equals(posterEmail)) {
            throw new RuntimeException("Only task poster can confirm delivery.");
        }

        if (task.getStatus() != TaskStatus.DELIVERED) {
            throw new RuntimeException("Task is not delivered yet.");
        }

        DeliveryProof dp = dpRepo.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("No delivery proof submitted"));

        boolean otpValid = false;

        if (dp.getOtpHash() != null) {
            if (otp == null || otp.isBlank())
                throw new RuntimeException("OTP is required");

            if (dp.getOtpExpiresAt().isBefore(Instant.now()))
                throw new RuntimeException("OTP expired");

            otpValid = BCrypt.checkpw(otp, dp.getOtpHash());
            if (!otpValid)
                throw new RuntimeException("Invalid OTP");
        }

        if (dp.getOtpHash() == null && dp.getPhotoUrl() != null) {
            otpValid = true;
        }

        if (!otpValid) {
            throw new RuntimeException("Could not validate delivery proof.");
        }

        // ✅ Mark proof verified
        dp.setVerifiedAt(Instant.now());
        dp.setVerifiedBy(task.getPoster().getId());
        dpRepo.save(dp);

        // ✅ Fetch HELD payment only
        Payment payment = paymentRepository
                .findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD)
                .orElseThrow(() -> new RuntimeException("Held payment not found"));

        // ✅ Release via service (gateway-safe)
        Payment released = paymentService.releaseFunds(taskId);

        // ✅ Update task
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);

        // ✅ Update doer metrics
        AppUser doer = task.getAcceptedBy();
        doer.setTasksCompleted(doer.getTasksCompleted() + 1);
        doer.setTotalEarnings(doer.getTotalEarnings() + released.getAmount());
        userRepository.save(doer);

        ConfirmResponse resp = new ConfirmResponse();
        resp.setMessage("Delivery confirmed.");
        resp.setPaymentStatus(released.getStatus().name());
        return resp;
    }


    private TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setCategory(t.getCategory());
        r.setOfferedPrice(t.getOfferedPrice());
        r.setCommission(t.getCommission());
        r.setCity(t.getCity());
        r.setLatitude(t.getLatitude());
        r.setLongitude(t.getLongitude());
        r.setStatus(t.getStatus().name());
        r.setCreatedAt(t.getCreatedAt());
        r.setPosterId(t.getPoster() != null ? t.getPoster().getId() : null);
        r.setPosterName(t.getPoster() != null ? t.getPoster().getName() : null);
        r.setAcceptedById(
                t.getAcceptedBy() != null ? t.getAcceptedBy().getId() : null
        );


        return r;
    }
}
