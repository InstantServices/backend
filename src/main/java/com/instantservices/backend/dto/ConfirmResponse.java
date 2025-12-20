package com.instantservices.backend.dto;

public class ConfirmResponse {
    private String message;
    private String paymentStatus;
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus;}
}
