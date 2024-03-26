package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.JobDataRepository;
import com.tim.transactioncase.service.JobService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobDataRepository jobDataRepository;

    @Autowired
    public JobServiceImpl(JobDataRepository jobDataRepository) {
        this.jobDataRepository = jobDataRepository;
    }

    @Transactional
    public Job createJob(String jobInfo, List<Order> orders, JobStatus status, List<Shipment> shipments) {
        Job job = new Job();
        job.setJobInfo(jobInfo);
        job.setOrders(orders);
        job.setStatus(status);
        job.setShipments(shipments);

        return jobDataRepository.save(job);
    }

    public Job save(Job job) {
        return jobDataRepository.save(job);
    }

    public List<Job> findAllByStatus(JobStatus status ){
        return jobDataRepository.findAllByStatusIn(List.of(status));
    }

    public Job findJobById(Long jobId) {
        Optional<Job> job = jobDataRepository.findById(jobId);
        return job.orElse(null);
    }
}
