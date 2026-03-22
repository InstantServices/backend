package com.instantservices.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
//
//    public void sendOtpEmail(String toEmail, String otp) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(toEmail);
//        message.setSubject("Delivery OTP Verification");
//
//        message.setText(
//                "Hello,\n\n" +
//                        "Your OTP for confirming delivery is: " + otp + "\n\n" +
//                        "This OTP is valid for 15 minutes.\n\n" +
//                        "Do not share this OTP with anyone.\n\n" +
//                        "Regards,\nInstant Services Team"
//        );
//
//        mailSender.send(message);
//        System.out.println("Email sent successfully!");
//    }
public void sendOtpEmail(String toEmail, String otp) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("vijayalakshmigogineni2005@gmail.com"); // MUST MATCH VERIFIED SENDER
        message.setTo(toEmail);
        message.setSubject("Delivery OTP Verification");
        message.setText(
                "Hello,\n\n" +
                        "Your OTP for confirming delivery is: " + otp + "\n\n" +
                        "This OTP is valid for 15 minutes.\n\n" +
                        "Regards,\nInstant Services Team"
        );

        mailSender.send(message);

        System.out.println("Email sent successfully to " + toEmail);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}