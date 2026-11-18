package com.freightpulse.controller;

import com.freightpulse.dto.ApiResponse;
import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.dto.OrderRequest;
import com.freightpulse.dto.VehicleRequest;
import com.freightpulse.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Dispatch", description = "Endpoints for orders, vehicles, and dispatch planning")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/orders")
    @Operation(
            summary = "Submit delivery orders",
            description = "Accepts a list of delivery orders and replaces any previously stored orders.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = OrderRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n  \"orders\": [\n    {\n      \"orderId\": \"ORD001\",\n      \"latitude\": 12.9716,\n      \"longitude\": 77.5946,\n      \"address\": \"MG Road, Bangalore, Karnataka, India\",\n      \"packageWeight\": 10,\n      \"priority\": \"HIGH\"\n    }\n  ]\n}"
                            )
                    )
            )
    )
    public ResponseEntity<ApiResponse> submitOrders(@Valid @RequestBody OrderRequest request) {
        ApiResponse response = dispatchService.saveOrders(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vehicles")
    @Operation(
            summary = "Submit vehicle fleet",
            description = "Accepts a list of vehicles and replaces any previously stored vehicles.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = VehicleRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n  \"vehicles\": [\n    {\n      \"vehicleId\": \"VEH001\",\n      \"capacity\": 100,\n      \"currentLatitude\": 12.9716,\n      \"currentLongitude\": 77.6413,\n      \"currentAddress\": \"Indiranagar, Bangalore, Karnataka, India\"\n    }\n  ]\n}"
                            )
                    )
            )
    )
    public ResponseEntity<ApiResponse> submitVehicles(@Valid @RequestBody VehicleRequest request) {
        ApiResponse response = dispatchService.saveVehicles(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    @Operation(
            summary = "Generate dispatch plan",
            description = "Calculates order assignments for vehicles based on capacity, priority, and location."
    )
    public ResponseEntity<DispatchPlanResponse> getPlan() {
        DispatchPlanResponse response = dispatchService.generatePlan();
        return ResponseEntity.ok(response);
    }
}
