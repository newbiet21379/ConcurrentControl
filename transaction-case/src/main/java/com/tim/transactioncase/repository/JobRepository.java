package com.tim.transactioncase.repository;

import com.tim.transactioncase.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
