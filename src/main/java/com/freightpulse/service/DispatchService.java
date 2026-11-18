package com.freightpulse.service;

import com.freightpulse.dto.ApiResponse;
import com.freightpulse.dto.DeliveryOrderDto;
import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.dto.OrderRequest;
import com.freightpulse.dto.VehicleDto;
import com.freightpulse.dto.VehicleRequest;
import com.freightpulse.exception.DispatchException;
import com.freightpulse.model.DeliveryOrder;
import com.freightpulse.model.Vehicle;
import com.freightpulse.repository.OrderRepository;
import com.freightpulse.repository.VehicleRepository;
import com.freightpulse.strategy.DispatchStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchService.class);

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchStrategy dispatchStrategy;

    public DispatchService(OrderRepository orderRepository, VehicleRepository vehicleRepository, DispatchStrategy dispatchStrategy) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.dispatchStrategy = dispatchStrategy;
    }

    public ApiResponse saveOrders(OrderRequest request) {
        List<DeliveryOrderDto> dtos = request.getOrders();
        Set<String> seenIds = new HashSet<>();
        List<DeliveryOrder> orders = new ArrayList<>();

        for (DeliveryOrderDto dto : dtos) {
            if (!seenIds.add(dto.getOrderId())) {
                throw new DispatchException("Duplicate orderId found: " + dto.getOrderId());
            }
            orders.add(new DeliveryOrder(
                    dto.getOrderId(),
                    dto.getLatitude(),
                    dto.getLongitude(),
                    dto.getAddress(),
                    dto.getPackageWeight(),
                    dto.getPriority()
            ));
        }

        orderRepository.saveAll(orders);
        log.info("Received {} orders", orders.size());
        return new ApiResponse("Delivery orders accepted.", "success");
    }

    public ApiResponse saveVehicles(VehicleRequest request) {
        List<VehicleDto> dtos = request.getVehicles();
        Set<String> seenIds = new HashSet<>();
        List<Vehicle> vehicles = new ArrayList<>();

        for (VehicleDto dto : dtos) {
            if (!seenIds.add(dto.getVehicleId())) {
                throw new DispatchException("Duplicate vehicleId found: " + dto.getVehicleId());
            }
            vehicles.add(new Vehicle(
                    dto.getVehicleId(),
                    dto.getCapacity(),
                    dto.getCurrentLatitude(),
                    dto.getCurrentLongitude(),
                    dto.getCurrentAddress()
            ));
        }

        vehicleRepository.saveAll(vehicles);
        log.info("Received {} vehicles", vehicles.size());
        return new ApiResponse("Vehicle details accepted.", "success");
    }

    public DispatchPlanResponse generatePlan() {
        if (orderRepository.isEmpty()) {
            throw new DispatchException("No delivery orders found. Please upload orders before generating a dispatch plan.");
        }
        if (vehicleRepository.isEmpty()) {
            throw new DispatchException("No vehicles found. Please upload vehicles before generating a dispatch plan.");
        }

        List<DeliveryOrder> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        DispatchPlanResponse response = dispatchStrategy.dispatch(orders, vehicles);
        log.info("Generated dispatch plan for {} orders across {} vehicles", orders.size(), vehicles.size());
        return response;
    }
}
