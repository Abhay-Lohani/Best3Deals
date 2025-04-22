package com.group5.best3deals.common.dto.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success) {
        this.success = success;
        this.message = success ? "success" : "error";
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.message = success ? "success" : "error";
        this.data = data;
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}