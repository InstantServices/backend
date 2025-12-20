package com.instantservices.backend.repository;


import com.instantservices.backend.model.Task;
import com.instantservices.backend.model.TaskStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Task t where t.id = :id")
    Optional<Task> findByIdForUpdate(@Param("id") Long id);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByCityAndStatus(String city, TaskStatus status, Pageable pageable);

    Page<Task> findByCategoryAndStatus(String category, TaskStatus status, Pageable pageable);
}
