package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Column(name = "p_id")
    private Long pId;
    private String shipmentInfo;
    private Integer sequence;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "shipment")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @ManyToOne
    @JsonBackReference
    private Job job;
}
