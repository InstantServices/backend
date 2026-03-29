package com.instantservices.backend.controller;


import com.instantservices.backend.model.Dispute;
import com.instantservices.backend.service.DisputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    // STEP 3 — Raise dispute
    @PostMapping("/{taskId}")
    public ResponseEntity<?> raiseDispute(
            @PathVariable Long taskId,
            @RequestParam String reason,
            @RequestParam String description) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Dispute dispute= disputeService.raiseDispute(taskId, email, reason, description);

        return ResponseEntity.ok(dispute);
    }

    // STEP 4 — Admin resolves dispute
    @PostMapping("/{disputeId}/resolve")
    public ResponseEntity<?> resolveDispute(
            @PathVariable Long disputeId,
            @RequestParam String decision) {

        String resp = disputeService.resolveDispute(disputeId, decision);
        return ResponseEntity.ok(resp);
    }
}