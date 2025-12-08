package com.instantservices.backend.controller;



import com.instantservices.backend.dto.CreateTaskRequest;
import com.instantservices.backend.dto.TaskResponse;
import com.instantservices.backend.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) { this.taskService = taskService; }

    // Create task (authenticated)
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest req) {
        TaskResponse resp = taskService.createTask(req);
        return ResponseEntity.ok(resp);
    }

    // List tasks with pagination and optional filters
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city
    ) {
        Page<TaskResponse> resp = taskService.listOpenTasks(page, size, category, city);
        return ResponseEntity.ok(resp);
    }

    // Get specific task
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse resp = taskService.getTask(id);
        return ResponseEntity.ok(resp);
    }
}
