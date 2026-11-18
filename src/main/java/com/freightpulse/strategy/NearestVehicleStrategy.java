package com.freightpulse.strategy;

import com.freightpulse.dto.DeliveryOrderDto;
import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.dto.UnassignedOrder;
import com.freightpulse.dto.VehicleDispatchPlan;
import com.freightpulse.model.DeliveryOrder;
import com.freightpulse.model.Priority;
import com.freightpulse.model.Vehicle;
import com.freightpulse.util.HaversineUtil;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class NearestVehicleStrategy implements DispatchStrategy {

    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

    @Override
    public DispatchPlanResponse dispatch(List<DeliveryOrder> orders, List<Vehicle> vehicles) {
        if (orders == null || orders.isEmpty() || vehicles == null || vehicles.isEmpty()) {
            return new DispatchPlanResponse(new ArrayList<>(), new ArrayList<>());
        }

        // track working vehicle state without modifying original objects
        List<VehicleState> vehicleStates = new ArrayList<>();
        int maxCapacity = 0;
        for (Vehicle v : vehicles) {
            vehicleStates.add(new VehicleState(v));
            if (v.getCapacity() > maxCapacity) {
                maxCapacity = v.getCapacity();
            }
        }

        // sort by priority first (HIGH, MEDIUM, LOW), then heavier packages first
        List<DeliveryOrder> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(Comparator
                .comparingInt((DeliveryOrder o) -> getPriorityRank(o.getPriority()))
                .thenComparing((o1, o2) -> Integer.compare(o2.getPackageWeight(), o1.getPackageWeight())));

        List<UnassignedOrder> unassignedOrders = new ArrayList<>();

        for (DeliveryOrder order : sortedOrders) {
            if (order.getPackageWeight() > maxCapacity) {
                unassignedOrders.add(new UnassignedOrder(order.getOrderId(), "Weight exceeds the capacity of every vehicle"));
                continue;
            }

            VehicleState bestVehicle = null;
            double shortestDistance = Double.MAX_VALUE;

            for (VehicleState vs : vehicleStates) {
                if (vs.remainingCapacity() >= order.getPackageWeight()) {
                    double dist = HaversineUtil.distance(vs.currentLat, vs.currentLon, order.getLatitude(), order.getLongitude());

                    // tie-break: keep vehicle with lower id if distances are identical
                    if (dist < shortestDistance || (Double.compare(dist, shortestDistance) == 0 && bestVehicle != null && vs.vehicleId.compareTo(bestVehicle.vehicleId) < 0)) {
                        shortestDistance = dist;
                        bestVehicle = vs;
                    }
                }
            }

            if (bestVehicle != null) {
                bestVehicle.assignOrder(order, shortestDistance);
            } else {
                unassignedOrders.add(new UnassignedOrder(order.getOrderId(), "Insufficient remaining capacity across fleet"));
            }
        }

        List<VehicleDispatchPlan> planList = new ArrayList<>();
        for (VehicleState vs : vehicleStates) {
            planList.add(new VehicleDispatchPlan(
                    vs.vehicleId,
                    vs.totalLoad,
                    DISTANCE_FORMAT.format(vs.totalDistance) + " km",
                    vs.assignedOrders
            ));
        }

        return new DispatchPlanResponse(planList, unassignedOrders);
    }

    private int getPriorityRank(Priority priority) {
        if (priority == null) {
            return 4;
        }
        return switch (priority) {
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    private static class VehicleState {
        private final String vehicleId;
        private final int capacity;
        private int totalLoad;
        private double currentLat;
        private double currentLon;
        private double totalDistance;
        private final List<DeliveryOrderDto> assignedOrders;

        public VehicleState(Vehicle v) {
            this.vehicleId = v.getVehicleId();
            this.capacity = v.getCapacity();
            this.totalLoad = 0;
            this.currentLat = v.getCurrentLatitude();
            this.currentLon = v.getCurrentLongitude();
            this.totalDistance = 0.0;
            this.assignedOrders = new ArrayList<>();
        }

        public int remainingCapacity() {
            return capacity - totalLoad;
        }

        public void assignOrder(DeliveryOrder order, double distance) {
            this.totalLoad += order.getPackageWeight();
            this.totalDistance += distance;
            this.currentLat = order.getLatitude();
            this.currentLon = order.getLongitude();
            this.assignedOrders.add(new DeliveryOrderDto(
                    order.getOrderId(),
                    order.getLatitude(),
                    order.getLongitude(),
                    order.getAddress(),
                    order.getPackageWeight(),
                    order.getPriority()
            ));
        }
    }
}
