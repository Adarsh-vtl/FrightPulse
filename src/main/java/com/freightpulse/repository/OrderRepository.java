package com.freightpulse.repository;

import com.freightpulse.model.DeliveryOrder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class OrderRepository {

    private final List<DeliveryOrder> orders = new CopyOnWriteArrayList<>();

    public void saveAll(List<DeliveryOrder> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
    }

    public List<DeliveryOrder> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(orders));
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public int count() {
        return orders.size();
    }

    public void clear() {
        orders.clear();
    }
}
