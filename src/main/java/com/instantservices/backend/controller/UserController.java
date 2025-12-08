package com.instantservices.backend.controller;



import com.instantservices.backend.dto.UpdateProfileRequest;
import com.instantservices.backend.dto.UserProfileResponse;
import com.instantservices.backend.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserProfileService profileService;

    public UserController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    // Get current user's profile
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        UserProfileResponse resp = profileService.getMyProfile();
        return ResponseEntity.ok(resp);
    }

    // Update current user's profile
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest req) {
        UserProfileResponse resp = profileService.updateMyProfile(req);
        return ResponseEntity.ok(resp);
    }

    // Public: get any user's public profile by id (task poster will use this)
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        UserProfileResponse resp = profileService.getUserById(id);
        // You may choose to hide sensitive fields before returning
        return ResponseEntity.ok(resp);
    }
}
