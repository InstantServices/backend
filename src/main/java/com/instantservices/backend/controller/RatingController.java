package com.instantservices.backend.controller;

import com.instantservices.backend.dto.RatingRequest;
import com.instantservices.backend.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<?> rateUser(@PathVariable Long taskId,
                                      @RequestBody RatingRequest req) {

        String email = getCurrentUserEmail();
        String resp = ratingService.rateUser(taskId, req.getScore(), req.getReview(), email);
        return ResponseEntity.ok(resp);
    }
}