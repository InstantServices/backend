package com.instantservices.backend.service;

import com.instantservices.backend.dto.OfferRequest;
import com.instantservices.backend.dto.OfferResponse;
import com.instantservices.backend.model.*;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.OfferRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;
    private final PaymentService paymentService;

    public OfferService(OfferRepository offerRepository,
                        TaskRepository taskRepository,
                        AppUserRepository userRepository,
                        PaymentService paymentService) {
        this.offerRepository = offerRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    // -------------------------
    //  ✔ toResponse()
    // -------------------------
    public OfferResponse toResponse(Offer offer) {
        OfferResponse resp = new OfferResponse();
        resp.setId(offer.getId());
        resp.setTaskId(offer.getTask().getId());
        resp.setUserId(offer.getUser().getId());
        resp.setUserName(offer.getUser().getName());
        resp.setMessage(offer.getMessage());
        resp.setExpectedTime(offer.getExpectedTime());
        resp.setProposedCommission(offer.getProposedCommission());
        resp.setStatus(offer.getStatus());
        return resp;
    }

    // -------------------------
    // ✔ Send offer
    // -------------------------
    public OfferResponse sendOffer(OfferRequest request, String email) {

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Offer offer = new Offer();
        offer.setTask(task);
        offer.setUser(user);
        offer.setMessage(request.getMessage());
        offer.setExpectedTime(request.getExpectedTime());
        offer.setProposedCommission(request.getProposedCommission());
        offer.setStatus("PENDING");

        Offer saved = offerRepository.save(offer);
        return toResponse(saved);
    }

    // -------------------------
    //  ✔ Get all offers of a task
    // -------------------------
    public List<OfferResponse> getOffersForTask(Long taskId) {
        return offerRepository.findByTaskId(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -------------------------
    //  ✔ Accept offer
    // -------------------------
    @Transactional
    public Offer acceptOffer(Long offerId, String currentUserEmail) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        // lock the task
        Task task = taskRepository.findByIdForUpdate(offer.getTask().getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Only poster can accept
        if (!task.getPoster().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You are not allowed to accept this offer. Only the task poster can accept.");
        }

        if (task.getStatus() != TaskStatus.OPEN) {
            throw new RuntimeException("This task is already accepted or closed.");
        }

        if (!"PENDING".equals(offer.getStatus())) {
            throw new RuntimeException("This offer is not pending.");
        }

        // Accept chosen offer
        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);

        // Reject all other offers for this task
        List<Offer> others = offerRepository.findByTaskId(task.getId());
        for (Offer o : others) {
            if (!o.getId().equals(offer.getId()) && !"REJECTED".equals(o.getStatus())) {
                o.setStatus("REJECTED");
                offerRepository.save(o);
            }
        }

        // Assign the doer and mark task accepted
        task.setStatus(TaskStatus.ACCEPTED);
        task.setAcceptedBy(offer.getUser());
        taskRepository.save(task);

        // Payment hold: idempotent (service will avoid duplicates)
        Double total = (task.getOfferedPrice() == null ? 0 : task.getOfferedPrice())
                + (task.getCommission() == null ? 0 : task.getCommission());

        Payment payment = paymentService.holdFunds(
                task.getId(),
                task.getPoster().getId(),
                offer.getUser().getId(),
                total
        );

        // return updated offer (refreshed)
        return offer;
    }

}
