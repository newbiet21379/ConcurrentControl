package com.tim.concurrentcontrol.repository;

import com.tim.concurrentcontrol.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
