package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.request.CreateJobFlowRequest;

import java.util.List;
import java.util.Map;

public interface JobService {

    public List<Job> createJobFlow(List<CreateJobFlowRequest> createJobRequests);
    Job save(Job job);

    List<Job> findAllByStatus(JobStatus status);

    Job findJobById(Long jobId);

    boolean existAllByPIds(List<Long> jobIds);


    Job createJob( List<Order> orders, JobStatus status, List<Shipment> shipments, String presetLine, Driver driver);

    Job confirmJob(Long jobId);

}