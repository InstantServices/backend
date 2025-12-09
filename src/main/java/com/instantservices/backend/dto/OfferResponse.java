package com.instantservices.backend.dto;

public class OfferResponse {

    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private String message;
    private String expectedTime;
    private Double proposedCommission;
    private String status;

    public OfferResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Double getProposedCommission() {
        return proposedCommission;
    }

    public void setProposedCommission(Double proposedCommission) {
        this.proposedCommission = proposedCommission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
// Getters and setters
    // (same as you already wrote)
}
