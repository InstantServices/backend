package com.instantservices.backend.controller;


import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.repository.AppUserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final AppUserRepository userRepository;


    public WalletController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //add money to wallet(fake payment)
    @PostMapping("/add")
    public String addMoney(@RequestParam Double amount, Principal principal) {
        AppUser user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setWalletBalance(user.getWalletBalance() + amount);
        userRepository.save(user);

        return "Money added to wallet. Current balance: " + user.getWalletBalance();
    }

    // Check wallet balance
    @GetMapping("/balance")
    public Double getBalance(Principal principal) {
        AppUser user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getWalletBalance();
    }

}
