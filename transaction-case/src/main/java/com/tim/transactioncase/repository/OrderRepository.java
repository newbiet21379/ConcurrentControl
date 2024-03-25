package com.tim.transactioncase.repository;

import com.tim.transactioncase.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
