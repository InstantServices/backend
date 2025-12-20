package com.instantservices.backend.repository;



import com.instantservices.backend.model.DeliveryProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryProofRepository extends JpaRepository<DeliveryProof, Long> {
    Optional<DeliveryProof> findByTaskIdAndType(Long taskId, String type);
    Optional<DeliveryProof> findByTaskId(Long taskId);


}
