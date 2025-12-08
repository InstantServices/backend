package com.instantservices.backend.repository;

import com.instantservices.backend.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByTaskId(Long taskId);
}
