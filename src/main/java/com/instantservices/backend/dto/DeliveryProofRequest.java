package com.instantservices.backend.dto;



import org.springframework.web.multipart.MultipartFile;

public class DeliveryProofRequest {
    private MultipartFile photo;   // optional
    private boolean generateOtp;   // if true → backend creates OTP

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public boolean isGenerateOtp() {
        return generateOtp;
    }

    public void setGenerateOtp(boolean generateOtp) {
        this.generateOtp = generateOtp;
    }
// getters and setters
}

