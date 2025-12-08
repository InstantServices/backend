package com.instantservices.backend.service;

import com.instantservices.backend.dto.OfferRequest;
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

    public OfferService(OfferRepository offerRepository, TaskRepository taskRepository, AppUserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Offer sendOffer(OfferRequest request, String email) {

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

        return offerRepository.save(offer);
    }

    public List<Offer> getOffersForTask(Long taskId) {
        return offerRepository.findByTaskId(taskId);
    }

    public Offer acceptOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        offer.setStatus("ACCEPTED");

        Task task = offer.getTask();
        task.setAcceptedBy(offer.getUser());
        task.setStatus(TaskStatus.ACCEPTED);

        taskRepository.save(task);
        return offerRepository.save(offer);
    }
}
