package com.tim.transactioncase.service;

import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService   {
    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment createShipment(String shipmentInfo, Order order, Driver driver, ShipmentStatus status) {
        Shipment shipment = new Shipment();
        shipment.setShipmentInfo(shipmentInfo);
        shipment.setOrder(order);
        shipment.setStatus(status);
        shipment.setDriver(driver);

        return shipmentRepository.save(shipment);
    }

    // Update function based on shipmentId
    public Shipment updateShipmentInfo(Long shipmentId, String shipmentInfo, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(RuntimeException::new);
        shipment.setShipmentInfo(shipmentInfo);
        shipment.setStatus(status);

        return shipmentRepository.save(shipment);
    }
}
