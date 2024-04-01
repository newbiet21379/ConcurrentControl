package com.tim.transactioncase.service;

import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;

import java.util.List;

public interface ShipmentService {
    Shipment createShipment(String shipmentInfo, Order order, ShipmentStatus status);
    void updateShipmentInfo(Long shipmentId, String shipmentInfo, ShipmentStatus status);

    List<Shipment> saveAll(List<Shipment> shipments);

    Shipment save(Shipment shipment);

}