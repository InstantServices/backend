package com.instantservices.backend.controller;




import com.instantservices.backend.dto.*;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        AppUser user = userService.register(request);

        RegisterResponse resp = new RegisterResponse();
        resp.setEmail(user.getEmail());
        resp.setMessage("User registered successfully");

        return resp;
    }

    @GetMapping("/test")
    public String test() {
        return "Auth Works!";
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token); // we’ll implement
        }

        return "Logged out successfully";
    }
    // ✅ NEW: REFRESH TOKEN ENDPOINT
    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return userService.refreshToken(request);
    }





}

