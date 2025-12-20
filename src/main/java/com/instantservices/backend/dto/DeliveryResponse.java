package com.instantservices.backend.dto;

public class DeliveryResponse {
    private String message;
    private String otp;
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
