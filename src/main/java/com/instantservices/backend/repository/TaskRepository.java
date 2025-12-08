package com.instantservices.backend.repository;


import com.instantservices.backend.model.Task;
import com.instantservices.backend.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    Page<Task> findByCategoryAndStatus(String category, TaskStatus status, Pageable pageable);
    Page<Task> findByCityAndStatus(String city, TaskStatus status, Pageable pageable);
}
