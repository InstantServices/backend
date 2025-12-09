package com.instantservices.backend.controller;

import com.instantservices.backend.config.JwtUtil;
import com.instantservices.backend.dto.OfferRequest;
import com.instantservices.backend.dto.OfferResponse;
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

    @PostMapping     // POST /api/offers
    public OfferResponse sendOffer(@RequestBody OfferRequest req,
                                   HttpServletRequest request) {
        String email = getEmail(request);
        return offerService.sendOffer(req, email);
    }

    @GetMapping("/task/{taskId}")   // GET /api/offers/task/3
    public List<OfferResponse> getOffersForTask(@PathVariable Long taskId) {
        return offerService.getOffersForTask(taskId);
    }

    @PostMapping("/{offerId}/accept")
    public OfferResponse acceptOffer(@PathVariable Long offerId, HttpServletRequest request) {
        String email = getEmail(request);
        Offer offer = offerService.acceptOffer(offerId, email);
        return offerService.toResponse(offer);
    }

}
