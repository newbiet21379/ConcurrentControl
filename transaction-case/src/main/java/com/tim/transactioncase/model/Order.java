package com.tim.transactioncase.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String orderInfo;

    @OneToMany(mappedBy = "order")
    private List<OrderExecute> orderDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private Shipment shipment;
}