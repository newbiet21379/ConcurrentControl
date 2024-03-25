package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job createJob(String jobInfo, List<Order> orders, JobStatus status, List<Shipment> shipments) {
        Job job = new Job();
        job.setJobInfo(jobInfo);
        job.setOrders(orders);
        job.setStatus(status);
        job.setShipments(shipments);

        return jobRepository.save(job);
    }

    public Job save(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> findAllByStatus(JobStatus status ){
        return jobRepository.findAllByStatusIn(List.of(status));
    }

    public Job findJobById(Long jobId) {
        Optional<Job> job = jobRepository.findById(jobId);
        return job.orElse(null);
    }
}
