package com.instantservices.backend.service;


import com.instantservices.backend.dto.*;
import com.instantservices.backend.model.*;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.DeliveryProofRepository;
import com.instantservices.backend.repository.PaymentRepository;
import com.instantservices.backend.repository.TaskRepository;
import jakarta.validation.constraints.Email;
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
    private final EmailService emailService;
    private final TrustScoreService trustScoreService;
    private final NotificationService notificationService;


    public TaskService(TaskRepository taskRepository,
                       AppUserRepository userRepository,
                       UserProfileService userProfileService, PaymentRepository paymentRepository, PaymentService paymentService, PasswordEncoder passwordEncoder, DeliveryProofRepository dpRepo, EmailService emailService, TrustScoreService trustScoreService, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.passwordEncoder = passwordEncoder;
        this.dpRepo = dpRepo;
        this.emailService = emailService;
        this.trustScoreService = trustScoreService;
        this.notificationService = notificationService;
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
        if(req.getDescription()==null||req.getDescription().trim().isEmpty())
        {
            throw new IllegalArgumentException("description of the work is required");
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
        //review the below line again
        poster.setTasksPosted((poster.getTasksPosted() == null ? 0 : poster.getTasksPosted()) + 1);
        userRepository.save(poster);

        return toResponse(saved);
    }

    //review this function
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
//    @Transactional
//    public DeliveryResponse markDelivered(Long taskId, DeliveryProofRequest req, String email) throws IOException {
//
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new RuntimeException("Task not found"));
//
//        if (task.getAcceptedBy() == null) {
//            throw new RuntimeException("Task is not yet accepted");
//        }
//
//        if (!task.getAcceptedBy().getEmail().equals(email)) {
//            throw new RuntimeException("Only assigned doer can deliver.");
//        }
//        if(req==null)
//        {
//            throw new RuntimeException("Request body missing");
//        }
//
//
//        System.out.println("DEBUG TASK:");
//        System.out.println("Task ID: " + task.getId());
//        System.out.println("AcceptedBy: " + (task.getAcceptedBy() != null ? task.getAcceptedBy().getEmail() : "NULL"));
//        System.out.println("Current User: " + email);
//
//        // Create DeliveryProof entry
//        DeliveryProof dp = new DeliveryProof();
//        dp.setTaskId(taskId);
//        dp.setDoerId(task.getAcceptedBy().getId());
//        dp.setCreatedAt(Instant.now());
//
//        // ============================
//        // 1) PHOTO PROOF (optional)
//        // ============================
//        // Ensure uploads directory exists
//        Path uploadDir = Paths.get("uploads");
//        if (!Files.exists(uploadDir)) {
//            Files.createDirectories(uploadDir);
//        }
//        if (req.getPhoto() != null && !req.getPhoto().isEmpty()) {
//            String fileName = System.currentTimeMillis() + "_" + req.getPhoto().getOriginalFilename();
//            Path path = Paths.get("uploads/" + fileName);
//            Files.copy(req.getPhoto().getInputStream(), path);
//            dp.setPhotoUrl("/uploads/" + fileName);
//            dp.setType("PHOTO");
//        }
//
//        // ============================
//        // 2) OTP PROOF (optional)
//        // ============================
//        String otp = null;
//        if (req.isGenerateOtp()) {
//
//            otp = String.valueOf(100000 + new Random().nextInt(900000));
//
//            dp.setOtpHash(passwordEncoder.encode(otp));
//            dp.setOtpExpiresAt(Instant.now().plusSeconds(900));
//
//            dp.setType(dp.getType() == null ? "OTP" : "PHOTO+OTP");
//
//            //  SEND OTP TO EMAIL
//            String userEmail = task.getPoster().getEmail();
//            emailService.sendOtpEmail(userEmail, otp);
//
//            //  Optional (for debugging only)
//            System.out.println("OTP sent to email: " + otp);
//        }
//
//        dpRepo.save(dp);
//
//        // Update task status
//        task.setStatus(TaskStatus.DELIVERED);
//        taskRepository.save(task);
//
//        // Response
//        DeliveryResponse resp = new DeliveryResponse();
//        resp.setMessage("Delivery proof submitted");
//        System.out.println("Generated OTP: " + otp);
//        //resp.setOtp(otp); // for dev only
//
//        return resp;
//    }

//    @Transactional
//    public ConfirmResponse confirmDelivery(Long taskId, String otp, String posterEmail) {
//
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new RuntimeException("Task not found"));
//
//        if (!task.getPoster().getEmail().equals(posterEmail)) {
//            throw new RuntimeException("Only task poster can confirm delivery.");
//        }
//
//        if (task.getStatus() != TaskStatus.DELIVERED) {
//            throw new RuntimeException("Task is not delivered yet.");
//        }
//
//        DeliveryProof dp = dpRepo.findByTaskId(taskId)
//                .orElseThrow(() -> new RuntimeException("No delivery proof submitted"));
//
//        boolean otpValid = false;
//
//        if (dp.getOtpHash() != null) {
//            if (otp == null || otp.isBlank())
//                throw new RuntimeException("OTP is required");
//
//            if (dp.getOtpExpiresAt().isBefore(Instant.now()))
//                throw new RuntimeException("OTP expired");
//
//            otpValid = BCrypt.checkpw(otp, dp.getOtpHash());
//            if (!otpValid)
//                throw new RuntimeException("Invalid OTP");
//        }
//
//        if (dp.getOtpHash() == null && dp.getPhotoUrl() != null) {
//            otpValid = true;
//        }
//
//        if (!otpValid) {
//            throw new RuntimeException("Could not validate delivery proof.");
//        }
//
//        // ✅ Mark proof verified
//        dp.setVerifiedAt(Instant.now());
//        dp.setVerifiedBy(task.getPoster().getId());
//        dpRepo.save(dp);
//
//        // ✅ Fetch HELD payment only
//        Payment payment = paymentRepository
//                .findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD)
//                .orElseThrow(() -> new RuntimeException("Held payment not found"));
//
//        // ✅ Release via service (gateway-safe)
//        Payment released = paymentService.releaseFunds(taskId);
//
//        // ✅ Update task
//        task.setStatus(TaskStatus.COMPLETED);
//        taskRepository.save(task);
//
//        // ✅ Update doer metrics
//        AppUser doer = task.getAcceptedBy();
//        doer.setTasksCompleted(doer.getTasksCompleted() + 1);
//        doer.setTotalEarnings(doer.getTotalEarnings() + released.getAmount());
//        userRepository.save(doer);
//
//        ConfirmResponse resp = new ConfirmResponse();
//        resp.setMessage("Delivery confirmed.");
//        resp.setPaymentStatus(released.getStatus().name());
//        return resp;
//    }


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


    @Transactional
    public String markArrived(Long taskId, String doerEmail) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getAcceptedBy() == null) {
            throw new RuntimeException("Task not accepted yet");
        }

        if (!task.getAcceptedBy().getEmail().equals(doerEmail)) {
            throw new RuntimeException("Only assigned doer can mark arrived");
        }

        if (task.getStatus() != TaskStatus.ACCEPTED) {
            throw new RuntimeException("Task is not in ACCEPTED state");
        }

        // Generate OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        DeliveryProof dp = new DeliveryProof();
        dp.setTaskId(taskId);
        dp.setDoerId(task.getAcceptedBy().getId());
        dp.setOtpHash(passwordEncoder.encode(otp));
        dp.setOtpExpiresAt(Instant.now().plusSeconds(900)); // 15 min
        dp.setCreatedAt(Instant.now());
        dp.setType("OTP");

        dpRepo.save(dp);


        notificationService.sendNotification(task.getPoster().getId(),"doer has arrived at the location");


        // Send OTP to poster email
        emailService.sendOtpEmail(task.getPoster().getEmail(), otp);

        // Update task status
        task.setStatus(TaskStatus.ARRIVED);
        task.setArrivedAt(Instant.now());
        taskRepository.save(task);

        return "OTP sent to poster. Ask poster for OTP to complete task.";
    }
    @Transactional
    public ConfirmResponse verifyOtp(Long taskId, String otp, String doerEmail) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAcceptedBy().getEmail().equals(doerEmail)) {
            throw new RuntimeException("Only doer can enter OTP");
        }

        if (task.getStatus() != TaskStatus.ARRIVED) {
            throw new RuntimeException("Task not in ARRIVED state");
        }

        DeliveryProof dp = dpRepo.findTopByTaskIdOrderByCreatedAtDesc(taskId)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (dp.getOtpExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!BCrypt.checkpw(otp, dp.getOtpHash())) {
            throw new RuntimeException("Invalid OTP");
        }
        // Step 1: Check HELD payment exists
        Payment payment = paymentRepository
                .findTopByTaskIdAndStatusOrderByCreatedAtDesc(taskId, PaymentStatus.HELD)
                .orElseThrow(() -> new RuntimeException("No held payment found. Payment must be held before releasing."));

            // Step 2: Release payment

        Payment released = paymentService.releaseFunds(taskId);


        // Complete task
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
        notificationService.sendNotification(task.getAcceptedBy().getId(),"otp verified ,payment released");

        // Update doer stats
        AppUser doer = task.getAcceptedBy();
        doer.setTasksCompleted(doer.getTasksCompleted() + 1);
        doer.setTotalEarnings(doer.getTotalEarnings() + released.getAmount());
        userRepository.save(doer);

        //update poster stats
        AppUser poster = task.getPoster();
        poster.setTasksCompleted(poster.getTasksCompleted()+1);
        userRepository.save(poster);




        trustScoreService.updateTrustScore(doer);
        trustScoreService.updateTrustScore(poster);

        ConfirmResponse resp = new ConfirmResponse();
        resp.setMessage("Task completed and payment released");
        resp.setPaymentStatus(released.getStatus().name());

        return resp;
    }

    @Transactional
    public String cancelTask(Long taskId, String email) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed task");
        }

        // If poster cancels
        if (task.getPoster().getEmail().equals(email)) {
            user.setCancellations(user.getCancellations() + 1);
            trustScoreService.updateTrustScore(user);
        }

        // If doer cancels
        if (task.getAcceptedBy() != null &&
                task.getAcceptedBy().getEmail().equals(email)) {

            user.setCancellations(user.getCancellations() + 1);
            trustScoreService.updateTrustScore(user);
        }

        task.setStatus(TaskStatus.CANCELLED);
        taskRepository.save(task);

        return "Task cancelled";
    }
}
