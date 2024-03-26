package com.tim.transactioncase.model;

import com.tim.transactioncase.common.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String shipmentInfo;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="order_id", nullable=false)
    private Order order;

    @OneToOne
    private Driver driver;
}
