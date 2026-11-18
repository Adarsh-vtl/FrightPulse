package com.freightpulse.dto;

import java.util.ArrayList;
import java.util.List;

public class DispatchPlanResponse {

    private List<VehicleDispatchPlan> dispatchPlan;
    private List<UnassignedOrder> unassignedOrders;

    public DispatchPlanResponse() {
        this.dispatchPlan = new ArrayList<>();
        this.unassignedOrders = new ArrayList<>();
    }

    public DispatchPlanResponse(List<VehicleDispatchPlan> dispatchPlan, List<UnassignedOrder> unassignedOrders) {
        this.dispatchPlan = dispatchPlan != null ? dispatchPlan : new ArrayList<>();
        this.unassignedOrders = unassignedOrders != null ? unassignedOrders : new ArrayList<>();
    }

    public List<VehicleDispatchPlan> getDispatchPlan() {
        return dispatchPlan;
    }

    public void setDispatchPlan(List<VehicleDispatchPlan> dispatchPlan) {
        this.dispatchPlan = dispatchPlan;
    }

    public List<UnassignedOrder> getUnassignedOrders() {
        return unassignedOrders;
    }

    public void setUnassignedOrders(List<UnassignedOrder> unassignedOrders) {
        this.unassignedOrders = unassignedOrders;
    }
}
