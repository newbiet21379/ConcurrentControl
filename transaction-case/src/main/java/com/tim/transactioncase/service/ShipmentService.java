package com.tim.transactioncase.service;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService   {
    @Autowired
    private ShipmentRepository shipmentRepository;

    public Shipment createShipment(String shipmentInfo, Order order, Driver driver) {
        Shipment shipment = new Shipment();
        shipment.setShipmentInfo(shipmentInfo);
        shipment.setOrder(order);
        shipment.setDriver(driver);

        return shipmentRepository.save(shipment);
    }

    // Update function based on shipmentId
    public Shipment updateShipmentInfo(Long shipmentId, String shipmentInfo) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(RuntimeException::new);
        shipment.setShipmentInfo(shipmentInfo);

        return shipmentRepository.save(shipment);
    }
}
