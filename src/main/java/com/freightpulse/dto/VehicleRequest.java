package com.freightpulse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class VehicleRequest {

    @NotEmpty(message = "Vehicles list cannot be empty")
    @Valid
    private List<VehicleDto> vehicles;

    public VehicleRequest() {
    }

    public VehicleRequest(List<VehicleDto> vehicles) {
        this.vehicles = vehicles;
    }

    public List<VehicleDto> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleDto> vehicles) {
        this.vehicles = vehicles;
    }
}
