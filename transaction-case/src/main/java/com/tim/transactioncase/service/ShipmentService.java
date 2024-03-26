package com.tim.transactioncase.service;

import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;

public interface ShipmentService {
    Shipment createShipment(String shipmentInfo, Order order, Driver driver, ShipmentStatus status);
    void updateShipmentInfo(Long shipmentId, String shipmentInfo, ShipmentStatus status);
}