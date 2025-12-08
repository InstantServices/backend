package com.instantservices.backend.service;


import com.instantservices.backend.dto.CreateTaskRequest;
import com.instantservices.backend.dto.TaskResponse;
import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.model.Task;
import com.instantservices.backend.model.TaskStatus;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;
    private final UserProfileService userProfileService;

    public TaskService(TaskRepository taskRepository,
                       AppUserRepository userRepository,
                       UserProfileService userProfileService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest req) {
        // Basic validation
        if (req.getCommission() == null || req.getCommission() < 0) {
            throw new IllegalArgumentException("Commission must be provided and >= 0");
        }
        if (req.getOfferedPrice() == null || req.getOfferedPrice() < 0) {
            throw new IllegalArgumentException("Offered price must be provided and >= 0");
        }
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        // Get current user (poster)
        AppUser poster = userProfileService.getCurrentAppUser()
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setCategory(req.getCategory());
        t.setOfferedPrice(req.getOfferedPrice());
        t.setCommission(req.getCommission());
        t.setCity(req.getCity());
        t.setLatitude(req.getLatitude());
        t.setLongitude(req.getLongitude());
        t.setPoster(poster);
        t.setStatus(TaskStatus.OPEN);

        Task saved = taskRepository.save(t);

        // update poster metrics
        poster.setTasksPosted((poster.getTasksPosted() == null ? 0 : poster.getTasksPosted()) + 1);
        userRepository.save(poster);

        return toResponse(saved);
    }

    public Page<TaskResponse> listOpenTasks(int page, int size, String category, String city) {
        PageRequest pr = PageRequest.of(page, size);
        Page<Task> pageRes;
        if (category != null && city != null) {
            pageRes = taskRepository.findByCategoryAndStatus(category, TaskStatus.OPEN, pr);
            // city filter not combined in repository method, use fallback
            pageRes = pageRes.map(t -> t).map(p -> p); // noop
        } else if (category != null) {
            pageRes = taskRepository.findByCategoryAndStatus(category, TaskStatus.OPEN, pr);
        } else if (city != null) {
            pageRes = taskRepository.findByCityAndStatus(city, TaskStatus.OPEN, pr);
        } else {
            pageRes = taskRepository.findByStatus(TaskStatus.OPEN, pr);
        }

        Page<TaskResponse> resp = pageRes.map(this::toResponse);
        return resp;
    }

    public TaskResponse getTask(Long id) {
        Task t = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return toResponse(t);
    }

    private TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setCategory(t.getCategory());
        r.setOfferedPrice(t.getOfferedPrice());
        r.setCommission(t.getCommission());
        r.setCity(t.getCity());
        r.setLatitude(t.getLatitude());
        r.setLongitude(t.getLongitude());
        r.setStatus(t.getStatus().name());
        r.setCreatedAt(t.getCreatedAt());
        r.setPosterId(t.getPoster() != null ? t.getPoster().getId() : null);
        r.setPosterName(t.getPoster() != null ? t.getPoster().getName() : null);
        r.setAcceptedById(
                t.getAcceptedBy() != null ? t.getAcceptedBy().getId() : null
        );


        return r;
    }
}
