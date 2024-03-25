package com.tim.transactioncase.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderExecute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String detailInfo;

    @ManyToOne
    private Order order;
}
