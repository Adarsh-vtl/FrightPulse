package com.freightpulse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class OrderRequest {

    @NotEmpty(message = "Orders list cannot be empty")
    @Valid
    private List<DeliveryOrderDto> orders;

    public OrderRequest() {
    }

    public OrderRequest(List<DeliveryOrderDto> orders) {
        this.orders = orders;
    }

    public List<DeliveryOrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<DeliveryOrderDto> orders) {
        this.orders = orders;
    }
}
