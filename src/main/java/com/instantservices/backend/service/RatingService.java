package com.instantservices.backend.service;

import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.model.Rating;
import com.instantservices.backend.model.Task;
import com.instantservices.backend.model.TaskStatus;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.RatingRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;
    private final TrustScoreService trustScoreService;

    public RatingService(RatingRepository ratingRepository,
                         TaskRepository taskRepository,
                         AppUserRepository userRepository, TrustScoreService trustScoreService) {
        this.ratingRepository = ratingRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.trustScoreService = trustScoreService;
    }

    @Transactional
    public String rateUser(Long taskId, int score, String review, String currentUserEmail) {

        if (score < 1 || score > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new RuntimeException("You can rate only after task completion");
        }

        AppUser fromUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppUser toUser;

        if (task.getPoster().getId().equals(fromUser.getId())) {
            toUser = task.getAcceptedBy();
        } else if (task.getAcceptedBy().getId().equals(fromUser.getId())) {
            toUser = task.getPoster();
        } else {
            throw new RuntimeException("You are not part of this task");
        }

        if (ratingRepository.existsByTaskIdAndFromUserId(taskId, fromUser.getId())) {
            throw new RuntimeException("You already rated for this task");
        }

        Rating rating = new Rating();
        rating.setTaskId(taskId);
        rating.setFromUserId(fromUser.getId());
        rating.setToUserId(toUser.getId());
        rating.setScore(score);
        rating.setReview(review);
        ratingRepository.save(rating);

        // ⭐ UPDATE USER RATING
        double totalScore = toUser.getAverageRating() * toUser.getTotalRatings();
        totalScore += score;

        toUser.setTotalRatings(toUser.getTotalRatings() + 1);
        toUser.setAverageRating(totalScore / toUser.getTotalRatings());
        trustScoreService.updateTrustScore(toUser);


        userRepository.save(toUser);


        return "Rating submitted";
    }
}