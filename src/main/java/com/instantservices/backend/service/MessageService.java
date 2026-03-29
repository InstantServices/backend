package com.instantservices.backend.service;

import com.instantservices.backend.model.AppUser;
import com.instantservices.backend.model.Message;
import com.instantservices.backend.model.Task;
import com.instantservices.backend.repository.AppUserRepository;
import com.instantservices.backend.repository.MessageRepository;
import com.instantservices.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;

    public MessageService(MessageRepository messageRepository, TaskRepository taskRepository, AppUserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    // Send Message
    public String sendMessage(Long taskId, String content, String senderEmail) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        AppUser sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only poster or doer can chat
        if (!sender.getId().equals(task.getPoster().getId()) &&
                !sender.getId().equals(task.getAcceptedBy().getId())) {
            throw new RuntimeException("Only poster and doer can send messages");
        }

        AppUser receiver;
        if (sender.getId().equals(task.getPoster().getId())) {
            receiver = task.getAcceptedBy();
        } else {
            receiver = task.getPoster();
        }

        Message msg = new Message();
        msg.setTaskId(taskId);
        msg.setSenderId(sender.getId());
        msg.setReceiverId(receiver.getId());
        msg.setContent(content);
        msg.setSentAt(Instant.now());

        messageRepository.save(msg);

        return "Message sent";
    }

    // Get Chat Messages
    public List<Message> getMessages(Long taskId, String email) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(task.getPoster().getId()) &&
                !user.getId().equals(task.getAcceptedBy().getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("You are not allowed to view this chat");
        }

        return messageRepository.findByTaskIdOrderBySentAtAsc(taskId);
    }
}
