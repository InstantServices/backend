package com.instantservices.backend.controller;

import com.instantservices.backend.dto.UpdateProfileRequest;
import com.instantservices.backend.dto.UserProfileResponse;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserProfileService profileService;
    private final AppUserRepository appUserRepository;

    public UserController(UserProfileService profileService, AppUserRepository appUserRepository) {
        this.profileService = profileService;
        this.appUserRepository = appUserRepository;
    }

    // Get current user's profile
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    // Update current user's profile
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(profileService.updateMyProfile(req));
    }

    // Public profile
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getUserById(id));
    }

    // Trust score
    @GetMapping("/trust-score/{userId}")
    public ResponseEntity<?> getTrustScore(@PathVariable Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user.getTrustScore());
    }
}