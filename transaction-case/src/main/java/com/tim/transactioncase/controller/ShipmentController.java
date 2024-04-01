package com.tim.transactioncase.controller;

import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.request.ShipmentDto;
import com.tim.transactioncase.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        Shipment createdShipment = shipmentService.createShipment(
                shipment.getShipmentInfo(),
                shipment.getOrder(),
                shipment.getStatus());

        return ResponseEntity.ok(createdShipment);
    }

    @PutMapping("/{shipmentId}")
    public ResponseEntity<Void> updateShipment(@PathVariable Long shipmentId,
                                               @RequestBody ShipmentDto shipmentDto) {
        shipmentService.updateShipmentInfo(shipmentId, shipmentDto.getShipmentInfo(), shipmentDto.getStatus());
        return ResponseEntity.ok().build();
    }
}