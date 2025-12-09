package com.instantservices.backend.dto;






public class OfferRequest {
    private Long taskId;
    private double proposedCommission;
    private String message;
    private String expectedTime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public double getProposedCommission() {
        return proposedCommission;
    }

    public void setProposedCommission(double proposedCommission) {
        this.proposedCommission = proposedCommission;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }
}
