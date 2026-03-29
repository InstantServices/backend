package com.instantservices.backend.controller;

import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.model.Dispute;
import com.instantservices.backend.model.Payment;
import com.instantservices.backend.model.Task;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.DisputeRepository;
import com.instantservices.backend.repository.PaymentRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DisputeRepository disputeRepository;
    private final AppUserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final TaskRepository taskRepository;

    public AdminController(DisputeRepository disputeRepository,
                           AppUserRepository userRepository, PaymentRepository paymentRepository, TaskRepository taskRepository) {
        this.disputeRepository = disputeRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/disputes")
    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    @GetMapping("/users")
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }


    //ban user manually
    @PostMapping("/ban/{userId}")
    public String banUser(@PathVariable Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        user.setRole("BANNED");
        userRepository.save(user);
        return "User banned";
    }

    //unban user manually
    @PostMapping("/unban/{userId}")
   public String unbanUser(@PathVariable Long userId){
        AppUser user = userRepository.findById(userId).orElseThrow();
        user.setBanned(false);
        userRepository.save(user);
        return "user unbanned";

   }
   @GetMapping("/tasks")
   public List<Task> getAllTasks(){
        return taskRepository.findAll();
   }

   @GetMapping("/payments")
   public List<Payment> getAllPayments(){
        return paymentRepository.findAll();
   }
    @GetMapping("/low-trust")
    public List<AppUser> lowTrustUsers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getTrustScore() < 40)
                .toList();
    }


}