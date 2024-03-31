package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tim.transactioncase.common.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "p_id")
    private Long pId;
    private String orderInfo;
    private OrderStatus orderStatus;
    private String presetLine;

    @OneToOne(cascade = CascadeType.ALL, mappedBy="order")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderExecute orderExecute;

    @OneToOne
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Shipment shipment;

    @OneToOne
    private Vehicle vehicle;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JsonBackReference
    private Job job;
}