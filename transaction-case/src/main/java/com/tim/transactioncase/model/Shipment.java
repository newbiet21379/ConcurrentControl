package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tim.transactioncase.common.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String shipmentInfo;
    private Integer sequence;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "shipment")
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JsonIgnore
    private Job job;
}
