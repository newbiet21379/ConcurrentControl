package com.tim.transactioncase.repository;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.driver = :driver")
    List<Order> getOrdersByDriver(@Param("driver") Driver driver);

    @Query("SELECT (:count = (select COUNT(o) FROM Order o " +
            "where o.shipment.driver = :driver)) ")
    boolean isOrderCountMatchedWithRequest(@Param("driver") Driver driver, @Param("count") Integer count);

    @Query("SELECT o FROM Order o WHERE o.shipment.driver.id IN :driverIds")
    List<Order> findByDriverIds(@Param("driverIds") List<Long> driverIds);
}
