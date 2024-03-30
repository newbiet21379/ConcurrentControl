package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.JobDataRepository;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobDataRepository jobDataRepository;

    @Autowired
    public JobServiceImpl(JobDataRepository jobDataRepository) {
        this.jobDataRepository = jobDataRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Job createJob(String jobInfo, List<Order> orders, JobStatus status, List<Shipment> shipments) {
        Job job = new Job();
        job.setJobInfo(jobInfo);
        job.setOrders(orders);
        job.setStatus(status);
        job.setShipments(shipments);

        return jobDataRepository.save(job);
    }

    @Override
    public List<Job> createJobFlow(List<CreateJobFlowRequest> createJobRequests) {
        return null;
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

    public List<Job> createJobNoTransactional(Map<Driver, List<Shipment>> driverListMap, String jobInfo, JobStatus status) {
        List<Job> jobs = new ArrayList<>();

        driverListMap.forEach((driver, shipments) -> {
            Job job = new Job();
            job.setJobInfo(jobInfo);
            job.setStatus(status);
            job.setShipments(shipments);
            job.setDriver(driver);

            jobs.add(job);
        });
        jobDataRepository.saveAll(jobs);

        return jobs;
    }
}
