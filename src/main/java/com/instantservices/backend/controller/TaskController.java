package com.instantservices.backend.controller;



import com.instantservices.backend.config.JwtUtil;
import com.instantservices.backend.dto.*;
import com.instantservices.backend.service.EmailService;
import com.instantservices.backend.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final EmailService emailService;


    public TaskController(TaskService taskService, JwtUtil jwtUtil, EmailService emailService) { this.taskService = taskService;

        this.emailService = emailService;
    }
    //  Helper method
    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Create task (authenticated)
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest req) {
        TaskResponse resp = taskService.createTask(req);
        return ResponseEntity.ok(resp);
    }

    // List tasks with pagination and optional filters
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city
    ) {
        Page<TaskResponse> resp = taskService.listOpenTasks(page, size, category, city);
        return ResponseEntity.ok(resp);
    }

    // Get specific task
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse resp = taskService.getTask(id);
        return ResponseEntity.ok(resp);
    }
    @PostMapping(value = "/{taskId}/deliver", consumes = "multipart/form-data")
    public ResponseEntity<?> markDelivered(
            @PathVariable Long taskId,
            @ModelAttribute DeliveryProofRequest req,
            HttpServletRequest request
    ) throws Exception {

        String email =  getCurrentUserEmail();
        DeliveryResponse resp = taskService.markDelivered(taskId, req, email);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{taskId}/confirm")
    public ResponseEntity<?> confirmDelivery(@PathVariable Long taskId,
                                             @RequestBody ConfirmDeliveryRequest req,
                                             HttpServletRequest request) {

        String email = getCurrentUserEmail();
        ConfirmResponse resp = taskService.confirmDelivery(taskId, req.getOtp(), email);

        return ResponseEntity.ok(resp);
    }
    @GetMapping("/test-mail")
    public String testMail() {
        emailService.sendOtpEmail("your_other_email@gmail.com", "123456");
        return "Mail sent";
    }


}
