package com.instantservices.backend.service;

import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class TrustScoreService {

    private final AppUserRepository userRepository;

    public TrustScoreService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateTrustScore(AppUser user) {

        double avgRating = user.getAverageRating() == null ? 0 : user.getAverageRating();
        int accepted = user.getTasksAccepted() == null ? 0 : user.getTasksAccepted();
        int completed = user.getTasksCompleted() == null ? 0 : user.getTasksCompleted();
        int disputes = user.getDisputes() == null ? 0 : user.getDisputes();
        int cancellations = user.getCancellations() == null ? 0 : user.getCancellations();
        int noResponse = user.getNoResponseCount() == null ? 0 : user.getNoResponseCount();

        double completionRate = accepted == 0 ? 0 : (double) completed / accepted;
        double disputeRate = accepted == 0 ? 0 : (double) disputes / accepted;
        double cancelRate = accepted == 0 ? 0 : (double) cancellations / accepted;
        double noResponseRate = accepted == 0 ? 0 : (double) noResponse / accepted;

        double ratingScore = avgRating * 20;
        double completionScore = completionRate * 100;
        double disputeScore = (1 - disputeRate) * 100;
        double cancelScore = (1 - cancelRate) * 100;
        double responseScore = (1 - noResponseRate) * 100;

        double trustScore =
                (ratingScore * 0.35) +
                        (completionScore * 0.30) +
                        (disputeScore * 0.15) +
                        (cancelScore * 0.10) +
                        (responseScore * 0.10);

        user.setTrustScore(trustScore);

        // AUTO BAN LOGIC
        if (trustScore < 20 ||user.getDisputes()>=3 ||user.getCancellations()>=5 ||user.getNoResponseCount()>=5) {
            user.setBanned(true);
            user.setRole("BANNED");
            System.out.println("User banned due to low trust score: " + user.getEmail());
        }
        else
        {
            user.setBanned(false);
        }

        userRepository.save(user);
    }

}