package com.tim.transactioncase.model;

import com.tim.transactioncase.common.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String jobInfo;
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private Driver driver;

    @OneToMany
    private List<Order> orders;

    @OneToMany
    private List<Shipment> shipments;
}