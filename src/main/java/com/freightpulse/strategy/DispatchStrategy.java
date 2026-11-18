package com.freightpulse.strategy;

import com.freightpulse.dto.DispatchPlanResponse;
import com.freightpulse.model.DeliveryOrder;
import com.freightpulse.model.Vehicle;

import java.util.List;

public interface DispatchStrategy {

    DispatchPlanResponse dispatch(List<DeliveryOrder> orders, List<Vehicle> vehicles);
}
