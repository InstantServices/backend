package com.instantservices.backend.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferRequest {
    private Long taskId;
    private double proposedCommission;
    private String message;
    private String expectedTime;
}
