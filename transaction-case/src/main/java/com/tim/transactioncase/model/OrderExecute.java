package com.tim.transactioncase.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "order_execute")
public class OrderExecute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "p_id")
    private Long pId;
    private String detailInfo;

    @OneToOne
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;
}
