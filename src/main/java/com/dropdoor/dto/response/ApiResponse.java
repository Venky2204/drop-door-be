package com.dropdoor.dto.response;

public class ApiResponse {
    private Boolean success;
    private String message;

    // Default constructor
    public ApiResponse() {
    }

    // Constructor with parameters
    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}