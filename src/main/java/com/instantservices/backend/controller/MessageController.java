package com.instantservices.backend.controller;

import com.instantservices.backend.dto.SendMessageRequest;
import com.instantservices.backend.model.Message;
import com.instantservices.backend.service.MessageService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    //send message
    @PostMapping
    public String sendMessage(@RequestBody SendMessageRequest req){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return messageService.sendMessage(req.getTaskId(), req.getContent(),email);

    }
    //get chat messages
    @GetMapping("/{taskId}")
    public List<Message> getMessages(@PathVariable Long taskId)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return messageService.getMessages(taskId,email);
    }
}
