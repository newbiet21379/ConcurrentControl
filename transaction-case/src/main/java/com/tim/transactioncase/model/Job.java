package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String presetLine;
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne
    @JsonManagedReference
    private Driver driver;

    @OneToMany(mappedBy = "job")
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(mappedBy = "job")
    @JsonManagedReference
    private List<Shipment> shipments;
}