package com.freightpulse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightpulse.dto.ApiResponse;
import com.freightpulse.dto.DeliveryOrderDto;
import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.dto.OrderRequest;
import com.freightpulse.dto.VehicleDispatchPlan;
import com.freightpulse.dto.VehicleDto;
import com.freightpulse.dto.VehicleRequest;
import com.freightpulse.model.Priority;
import com.freightpulse.service.DispatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({DispatchController.class, RootController.class})
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DispatchService dispatchService;

    @Test
    void submitOrdersSuccess() throws Exception {
        DeliveryOrderDto order = new DeliveryOrderDto("ORD001", 12.9716, 77.5946, "MG Road, Bangalore", 10, Priority.HIGH);
        OrderRequest request = new OrderRequest(List.of(order));

        when(dispatchService.saveOrders(any())).thenReturn(new ApiResponse("Delivery orders accepted.", "success"));

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Delivery orders accepted."));
    }

    @Test
    void submitOrdersInvalidLatitudeFails() throws Exception {
        String invalidJson = """
                {
                  "orders": [
                    {
                      "orderId": "ORD1",
                      "latitude": 999.0,
                      "longitude": 77.0,
                      "address": "Sample",
                      "packageWeight": 10,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void submitVehiclesSuccess() throws Exception {
        VehicleDto vehicle = new VehicleDto("VEH001", 100, 12.9716, 77.6413, "Indiranagar, Bangalore");
        VehicleRequest request = new VehicleRequest(List.of(vehicle));

        when(dispatchService.saveVehicles(any())).thenReturn(new ApiResponse("Vehicle details accepted.", "success"));

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Vehicle details accepted."));
    }

    @Test
    void submitVehiclesZeroCapacityFails() throws Exception {
        String invalidJson = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH1",
                      "capacity": 0,
                      "currentLatitude": 12.9,
                      "currentLongitude": 77.6,
                      "currentAddress": "Sample"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void getPlanSuccess() throws Exception {
        VehicleDispatchPlan vPlan = new VehicleDispatchPlan("VEH001", 10, "5 km", Collections.emptyList());
        DispatchPlanResponse response = new DispatchPlanResponse(List.of(vPlan), Collections.emptyList());

        when(dispatchService.generatePlan()).thenReturn(response);

        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.dispatchPlan[0].totalDistance").value("5 km"));
    }

    @Test
    void rootRedirectsToSwagger() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }
}
