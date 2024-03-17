package com.tim.transactioncase.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String shipmentInfo;

    @OneToOne
    private Order order;

    @OneToOne
    private Driver driver;
}
