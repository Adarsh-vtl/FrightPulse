package com.freightpulse.dto;

import java.util.ArrayList;
import java.util.List;

public class VehicleDispatchPlan {

    private String vehicleId;
    private int totalLoad;
    private String totalDistance;
    private List<DeliveryOrderDto> assignedOrders;

    public VehicleDispatchPlan() {
        this.assignedOrders = new ArrayList<>();
    }

    public VehicleDispatchPlan(String vehicleId, int totalLoad, String totalDistance, List<DeliveryOrderDto> assignedOrders) {
        this.vehicleId = vehicleId;
        this.totalLoad = totalLoad;
        this.totalDistance = totalDistance;
        this.assignedOrders = assignedOrders != null ? assignedOrders : new ArrayList<>();
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(int totalLoad) {
        this.totalLoad = totalLoad;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<DeliveryOrderDto> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<DeliveryOrderDto> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}
