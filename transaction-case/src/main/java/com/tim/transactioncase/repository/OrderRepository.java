package com.tim.transactioncase.repository;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrderByIdIn(List<Long> ids);

    boolean existsAllBypIdIn(List<Long> pIds);
}
