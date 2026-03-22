package com.instantservices.backend.exception;


public class ErrorResponse {

    private String type;
    private String message;

    public ErrorResponse(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}