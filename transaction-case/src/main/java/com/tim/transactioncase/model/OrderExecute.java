package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_execute")
public class OrderExecute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String detailInfo;

    @OneToOne
    @JsonIgnore
    private Order order;
}
