package com.instantservices.backend.controller;




import com.instantservices.backend.dto.LoginRequest;
import com.instantservices.backend.dto.LoginResponse;
import com.instantservices.backend.dto.RegisterRequest;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AppUser register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }
    @GetMapping("/test")
    public String test() {
        return "Auth Works!";
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }



}

