package com.tim.transactioncase.repository;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobDataRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByStatusIn(List<JobStatus> statuses);

    Job findBypId(Long pId);

    boolean existsAllBypIdIn(List<Long> pIds);
}
