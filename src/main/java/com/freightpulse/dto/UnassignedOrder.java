package com.freightpulse.dto;

public class UnassignedOrder {

    private String orderId;
    private String reason;

    public UnassignedOrder() {
    }

    public UnassignedOrder(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
