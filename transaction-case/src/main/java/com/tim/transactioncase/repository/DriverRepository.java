package com.tim.transactioncase.repository;

import com.tim.transactioncase.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
