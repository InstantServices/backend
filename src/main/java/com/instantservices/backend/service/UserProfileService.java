package com.instantservices.backend.service;


import com.instantservices.backend.dto.UpdateProfileRequest;
import com.instantservices.backend.dto.UserProfileResponse;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserProfileService {

    private final AppUserRepository userRepository;

    public UserProfileService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // helper: get current user's AppUser
    public Optional<AppUser> getCurrentAppUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        String email = auth.getName(); // our UserDetails uses email as username
        return userRepository.findByEmail(email);
    }

    public UserProfileResponse getMyProfile() {
        AppUser user = getCurrentAppUser().orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    public UserProfileResponse getUserById(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest req) {
        AppUser user = getCurrentAppUser().orElseThrow(() -> new RuntimeException("User not found"));
        if (req.getName() != null) user.setName(req.getName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getCity() != null) user.setCity(req.getCity());
        if (req.getCountry() != null) user.setCountry(req.getCountry());
        if (req.getLatitude() != null) user.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) user.setLongitude(req.getLongitude());
        if (req.getBio() != null) user.setBio(req.getBio());
        // repository.save not necessary inside @Transactional when entity is managed
        userRepository.save(user);
        return toResponse(user);
    }

    // helper to map
    private UserProfileResponse toResponse(AppUser user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getCity(),
                user.getCountry(),
                user.getLatitude(),
                user.getLongitude(),
                user.getBio(),
                user.getReliabilityScore(),
                user.getTasksCompleted(),
                user.getTasksPosted(),
                user.getTotalEarnings()
        );
    }
}
