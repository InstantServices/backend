package com.instantservices.backend.model;

public enum PaymentStatus {
    PENDING,
    HELD,
    RELEASED,
    REFUNDED,
    FAILED,
    DISPUTED_HELD,
    DISPUTED_RELEASED
}