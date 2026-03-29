package com.instantservices.backend.repository;

import com.instantservices.backend.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating,Long> {
    List<Rating> findByToUserId(Long userId);
    boolean existsByTaskIdAndFromUserId(Long taskId,Long fromUserId);


}
