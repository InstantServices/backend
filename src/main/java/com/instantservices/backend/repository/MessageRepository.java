package com.instantservices.backend.repository;

import com.instantservices.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findByTaskIdOrderBySentAtAsc(Long taskId);
}
