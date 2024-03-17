package com.tim.transactioncase.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String jobInfo;
    private String status; // NEW: To capture status of the job

    @OneToMany
    private List<Order> orders;

    @OneToMany
    private List<Shipment> shipments;
}