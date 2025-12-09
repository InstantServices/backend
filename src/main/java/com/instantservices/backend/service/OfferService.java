package com.instantservices.backend.service;

import com.instantservices.backend.dto.OfferRequest;
import com.instantservices.backend.dto.OfferResponse;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.model.Offer;
import com.instantservices.backend.model.Task;
import com.instantservices.backend.model.TaskStatus;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.OfferRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;

    public OfferService(OfferRepository offerRepository,
                        TaskRepository taskRepository,
                        AppUserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Convert Offer Entity → OfferResponse DTO
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

    // User sends offer to a task
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

    // Get all offers for a task
    public List<OfferResponse> getOffersForTask(Long taskId) {
        return offerRepository.findByTaskId(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Task poster accepts an offer
    public Offer acceptOffer(Long offerId, String currentUserEmail) {

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        Task task = offer.getTask();

        // 1️⃣ Validate: Only task poster can accept
        if (!task.getPoster().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You are not allowed to accept this offer. Only the task poster can accept.");
        }

        // 2️⃣ Validate: Task must be OPEN
        if (task.getStatus() != TaskStatus.OPEN) {
            throw new RuntimeException("This task is already accepted or closed.");
        }

        // 3️⃣ Validate: Offer must be in PENDING status
        if (!offer.getStatus().equals("PENDING")) {
            throw new RuntimeException("This offer cannot be accepted because it is not pending.");
        }

        // 4️⃣ Accept the offer
        offer.setStatus("ACCEPTED");

        // 5️⃣ Update task status and assign doer
        task.setStatus(TaskStatus.ACCEPTED);
        task.setAcceptedBy(offer.getUser());

        // Save changes
        taskRepository.save(task);
        return offerRepository.save(offer);
    }

}
