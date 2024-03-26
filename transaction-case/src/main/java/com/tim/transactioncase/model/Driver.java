package com.tim.transactioncase.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @Column(unique = true)
    @Size(min = 11, max = 11, message = "Mobile number must be exactly 11 characters long")
    private String mobile;

    @OneToOne(cascade = CascadeType.ALL)
    private Vehicle vehicle;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Job> jobs;
}