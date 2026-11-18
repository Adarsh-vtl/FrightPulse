package com.freightpulse.repository;

import com.freightpulse.model.Vehicle;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class VehicleRepository {

    private final List<Vehicle> vehicles = new CopyOnWriteArrayList<>();

    public void saveAll(List<Vehicle> newVehicles) {
        vehicles.clear();
        if (newVehicles != null) {
            vehicles.addAll(newVehicles);
        }
    }

    public List<Vehicle> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(vehicles));
    }

    public boolean isEmpty() {
        return vehicles.isEmpty();
    }

    public int count() {
        return vehicles.size();
    }

    public void clear() {
        vehicles.clear();
    }
}
