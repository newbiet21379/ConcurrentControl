package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.ShipmentRepository;
import com.tim.transactioncase.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentServiceImpl(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment createShipment(String shipmentInfo, Order order, ShipmentStatus status) {
        Shipment shipment = new Shipment();
        shipment.setShipmentInfo(shipmentInfo);
        shipment.setOrder(order);
        shipment.setStatus(status);

        return shipmentRepository.save(shipment);
    }
    public Shipment save(Shipment shipments) {
        return shipmentRepository.save(shipments);
    }


    public List<Shipment> saveAll(List<Shipment> shipments) {
        return shipmentRepository.saveAll(shipments);
    }

    // Update function based on shipmentId
    public void updateShipmentInfo(Long shipmentId, String shipmentInfo, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(RuntimeException::new);
        shipment.setShipmentInfo(shipmentInfo);
        shipment.setStatus(status);

        shipmentRepository.save(shipment);
    }
}
