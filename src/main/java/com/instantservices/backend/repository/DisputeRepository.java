package com.instantservices.backend.repository;

import com.instantservices.backend.model.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisputeRepository extends JpaRepository<Dispute,Long> {


    Optional<Dispute> findByTaskId(Long taskId);
}
