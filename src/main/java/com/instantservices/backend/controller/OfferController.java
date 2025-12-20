package com.instantservices.backend.controller;

import com.instantservices.backend.config.JwtUtil;
import com.instantservices.backend.dto.OfferRequest;
import com.instantservices.backend.dto.OfferResponse;
import com.instantservices.backend.model.Offer;
import com.instantservices.backend.model.OfferAcceptResponse;
import com.instantservices.backend.model.Payment;
import com.instantservices.backend.model.PaymentStatus;
import com.instantservices.backend.repository.PaymentRepository;
import com.instantservices.backend.service.OfferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;
    private final JwtUtil jwtUtil;
    private final PaymentRepository paymentRepository;

    public OfferController(OfferService offerService, JwtUtil jwtUtil, PaymentRepository paymentRepository) {
        this.offerService = offerService;
        this.jwtUtil = jwtUtil;
        this.paymentRepository = paymentRepository;
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
    public OfferAcceptResponse acceptOffer(@PathVariable Long offerId,
                                           HttpServletRequest request) {

        String email = getEmail(request);
        Offer offer = offerService.acceptOffer(offerId, email);

        OfferResponse offerDto = offerService.toResponse(offer);

        // ✅ fetch HELD payment safely
        Payment payment = paymentRepository
                .findTopByTaskIdAndStatusOrderByCreatedAtDesc(
                        offer.getTask().getId(),
                        PaymentStatus.HELD
                )
                .orElse(null);

        OfferAcceptResponse resp = new OfferAcceptResponse();
        resp.setOffer(offerDto);

        if (payment != null) {
            resp.setPaymentId(payment.getId());
            resp.setPaymentStatus(payment.getStatus().name());
            resp.setGatewayTxnId(payment.getGatewayTxnId());
            resp.setAmount(payment.getAmount());
        }

        return resp;
    }



}
