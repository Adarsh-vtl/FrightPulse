package com.freightpulse.strategy;

import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.dto.VehicleDispatchPlan;
import com.freightpulse.model.DeliveryOrder;
import com.freightpulse.model.Priority;
import com.freightpulse.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NearestVehicleStrategyTest {

    private NearestVehicleStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new NearestVehicleStrategy();
    }

    @Test
    void emptyInputsReturnEmptyPlan() {
        DispatchPlanResponse response1 = strategy.dispatch(null, null);
        assertNotNull(response1);
        assertTrue(response1.getDispatchPlan().isEmpty());

        DispatchPlanResponse response2 = strategy.dispatch(Collections.emptyList(), Collections.emptyList());
        assertNotNull(response2);
        assertTrue(response2.getDispatchPlan().isEmpty());
    }

    @Test
    void priorityOrderRespectedHighBeforeMediumAndLow() {
        Vehicle vehicle = new Vehicle("VEH1", 30, 28.6139, 77.2090, "Connaught Place");

        DeliveryOrder low = new DeliveryOrder("ORD_LOW", 28.6139, 77.2090, "Loc", 15, Priority.LOW);
        DeliveryOrder med = new DeliveryOrder("ORD_MED", 28.6139, 77.2090, "Loc", 15, Priority.MEDIUM);
        DeliveryOrder high = new DeliveryOrder("ORD_HIGH", 28.6139, 77.2090, "Loc", 15, Priority.HIGH);

        // pass low first in input list
        DispatchPlanResponse response = strategy.dispatch(List.of(low, med, high), List.of(vehicle));

        assertEquals(1, response.getDispatchPlan().size());
        VehicleDispatchPlan plan = response.getDispatchPlan().get(0);

        // vehicle capacity is 30, each order is 15 -> exactly two fit
        assertEquals(2, plan.getAssignedOrders().size());
        assertEquals("ORD_HIGH", plan.getAssignedOrders().get(0).getOrderId());
        assertEquals("ORD_MED", plan.getAssignedOrders().get(1).getOrderId());

        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD_LOW", response.getUnassignedOrders().get(0).getOrderId());
    }

    @Test
    void capacityNeverExceeded() {
        Vehicle vehicle = new Vehicle("VEH1", 50, 28.6139, 77.2090, "Connaught Place");

        DeliveryOrder o1 = new DeliveryOrder("ORD1", 28.6139, 77.2090, "Loc", 30, Priority.HIGH);
        DeliveryOrder o2 = new DeliveryOrder("ORD2", 28.6139, 77.2090, "Loc", 25, Priority.HIGH);

        DispatchPlanResponse response = strategy.dispatch(List.of(o1, o2), List.of(vehicle));

        VehicleDispatchPlan plan = response.getDispatchPlan().get(0);
        assertTrue(plan.getTotalLoad() <= 50);
        assertEquals(30, plan.getTotalLoad());
        assertEquals(1, plan.getAssignedOrders().size());
        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD2", response.getUnassignedOrders().get(0).getOrderId());
    }

    @Test
    void nearestVehicleChosen() {
        // VEH_CLOSE is right at the order location, VEH_FAR is far away
        Vehicle near = new Vehicle("VEH_NEAR", 100, 28.6139, 77.2090, "Connaught Place");
        Vehicle far = new Vehicle("VEH_FAR", 100, 28.4595, 77.0266, "Gurgaon");

        DeliveryOrder order = new DeliveryOrder("ORD1", 28.6140, 77.2091, "Nearby Connaught Place", 10, Priority.HIGH);

        DispatchPlanResponse response = strategy.dispatch(List.of(order), List.of(far, near));

        VehicleDispatchPlan nearPlan = response.getDispatchPlan().stream()
                .filter(p -> p.getVehicleId().equals("VEH_NEAR"))
                .findFirst()
                .orElseThrow();
        assertEquals(1, nearPlan.getAssignedOrders().size());
        assertEquals("ORD1", nearPlan.getAssignedOrders().get(0).getOrderId());

        VehicleDispatchPlan farPlan = response.getDispatchPlan().stream()
                .filter(p -> p.getVehicleId().equals("VEH_FAR"))
                .findFirst()
                .orElseThrow();
        assertEquals(0, farPlan.getAssignedOrders().size());
    }

    @Test
    void unassignableOrderExceedingAllCapacityGoesToUnassigned() {
        Vehicle vehicle = new Vehicle("VEH1", 50, 28.6139, 77.2090, "Connaught Place");
        DeliveryOrder heavyOrder = new DeliveryOrder("ORD_HEAVY", 28.6139, 77.2090, "Loc", 60, Priority.HIGH);

        DispatchPlanResponse response = strategy.dispatch(List.of(heavyOrder), List.of(vehicle));

        assertEquals(0, response.getDispatchPlan().get(0).getAssignedOrders().size());
        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD_HEAVY", response.getUnassignedOrders().get(0).getOrderId());
        assertEquals("Weight exceeds the capacity of every vehicle", response.getUnassignedOrders().get(0).getReason());
    }
}
