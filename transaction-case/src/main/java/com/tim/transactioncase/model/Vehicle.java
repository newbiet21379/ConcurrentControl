package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String model;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Order order;
}