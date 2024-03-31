package com.tim.transactioncase.utils;

import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShipmentMapper {

    public static List<Shipment> toShipmentList(List<Order> orders, List<Driver> drivers, ShipmentStatus status) {
        if (orders.size() != drivers.size()) {
            throw new IllegalStateException("Mismatch in orders and drivers counts.");
        }

        return IntStream.range(0, orders.size())
                .mapToObj(i ->
                        toShipment(orders.get(i), drivers.get(i), status))
                .collect(Collectors.toList());
    }

    public static Shipment toShipment(Order order, Driver driver, ShipmentStatus status) {
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setStatus(status);
        return shipment;
    }
}