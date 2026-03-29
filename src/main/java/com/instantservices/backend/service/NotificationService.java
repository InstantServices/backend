package com.instantservices.backend.service;

import com.instantservices.backend.model.Notification;
import com.instantservices.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public void sendNotification(Long userId,String message){
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        notificationRepository.save(n);

    }


}
