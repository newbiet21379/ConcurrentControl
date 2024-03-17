package com.tim.transactioncase.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToOne
    private Vehicle vehicle;

    @OneToMany
    private List<Job> jobs; // NEW: Driver can be assigned to multiple jobs.
}