package com.freightpulse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.dto.OrderRequest;
import com.freightpulse.dto.VehicleDispatchPlan;
import com.freightpulse.dto.VehicleRequest;
import com.freightpulse.repository.OrderRepository;
import com.freightpulse.repository.VehicleRepository;
import com.freightpulse.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SampleDataIntegrationTest {

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetState() {
        orderRepository.clear();
        vehicleRepository.clear();
    }

    @Test
    void sampleDataFleetCapacityNeverExceeded() throws Exception {
        ClassPathResource ordersResource = new ClassPathResource("samples/sample-orders.json");
        ClassPathResource vehiclesResource = new ClassPathResource("samples/sample-vehicles.json");

        try (InputStream is = ordersResource.getInputStream()) {
            OrderRequest orderRequest = objectMapper.readValue(is, OrderRequest.class);
            dispatchService.saveOrders(orderRequest);
        }

        VehicleRequest vehicleRequest;
        try (InputStream is = vehiclesResource.getInputStream()) {
            vehicleRequest = objectMapper.readValue(is, VehicleRequest.class);
            dispatchService.saveVehicles(vehicleRequest);
        }

        Map<String, Integer> vehicleCapacities = vehicleRequest.getVehicles().stream()
                .collect(Collectors.toMap(v -> v.getVehicleId(), v -> v.getCapacity()));

        DispatchPlanResponse planResponse = dispatchService.generatePlan();
        assertNotNull(planResponse);
        assertFalse(planResponse.getDispatchPlan().isEmpty());

        for (VehicleDispatchPlan vp : planResponse.getDispatchPlan()) {
            int maxCap = vehicleCapacities.get(vp.getVehicleId());
            assertTrue(vp.getTotalLoad() <= maxCap,
                    "Vehicle " + vp.getVehicleId() + " totalLoad " + vp.getTotalLoad() + " exceeds capacity " + maxCap);

            int calculatedLoad = vp.getAssignedOrders().stream()
                    .mapToInt(o -> o.getPackageWeight())
                    .sum();
            assertEquals(calculatedLoad, vp.getTotalLoad(),
                    "Mismatch in load sum for vehicle " + vp.getVehicleId());
        }

        int totalAssigned = planResponse.getDispatchPlan().stream()
                .mapToInt(vp -> vp.getAssignedOrders().size())
                .sum();
        int totalUnassigned = planResponse.getUnassignedOrders().size();
        assertEquals(30, totalAssigned + totalUnassigned, "Total orders must be 30");
    }
}
