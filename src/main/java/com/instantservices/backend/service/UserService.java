package com.instantservices.backend.service;

import com.instantservices.backend.config.JwtUtil;
import com.instantservices.backend.dto.*;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    public AppUser register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        AppUser user = new AppUser();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        //  ACCESS TOKEN
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        //  REFRESH TOKEN
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        //  REMOVE THIS (WRONG)
        // String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                accessToken,   //  FIX
                refreshToken   //  FIX
        );
    }

    // ✅ REFRESH TOKEN LOGIC
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        String email = jwtUtil.extractEmail(request.getRefreshToken());

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        RefreshTokenResponse resp = new RefreshTokenResponse();
        resp.setAccessToken(newAccessToken);

        return resp;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_"+user.getRole())
                .build();
    }
    public void logout(String token) {
        blacklistService.blacklistToken(token);
    }


}
