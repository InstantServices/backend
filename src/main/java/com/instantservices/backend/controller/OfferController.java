package com.instantservices.backend.controller;



import com.instantservices.backend.config.JwtUtil;
import com.instantservices.backend.dto.OfferRequest;
import com.instantservices.backend.model.Offer;
import com.instantservices.backend.service.OfferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;
    private final JwtUtil jwtUtil;

    public OfferController(OfferService offerService, JwtUtil jwtUtil) {
        this.offerService = offerService;
        this.jwtUtil = jwtUtil;
    }

    private String getEmail(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        return jwtUtil.extractEmail(auth.substring(7));
    }

    @PostMapping
    public Offer sendOffer(@RequestBody OfferRequest req, HttpServletRequest request) {
        String email = getEmail(request);
        return offerService.sendOffer(req, email);
    }

    @GetMapping("/task/{taskId}")
    public List<Offer> getOffersForTask(@PathVariable Long taskId) {
        return offerService.getOffersForTask(taskId);
    }

    @PostMapping("/{offerId}/accept")
    public Offer acceptOffer(@PathVariable Long offerId) {
        return offerService.acceptOffer(offerId);
    }
}
