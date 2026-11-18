package com.freightpulse.dto;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private String status;
    private String message;
    private List<String> details;

    public ErrorResponse() {
        this.status = "error";
        this.details = new ArrayList<>();
    }

    public ErrorResponse(String message, List<String> details) {
        this.status = "error";
        this.message = message;
        this.details = details != null ? details : new ArrayList<>();
    }

    public ErrorResponse(String message, String detail) {
        this.status = "error";
        this.message = message;
        this.details = new ArrayList<>();
        if (detail != null) {
            this.details.add(detail);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
