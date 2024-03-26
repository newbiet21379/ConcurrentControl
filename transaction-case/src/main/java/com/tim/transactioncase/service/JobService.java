package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;

import java.util.List;

public interface JobService {

    Job createJob(String jobInfo, List<Order> orders, JobStatus status, List<Shipment> shipments);

    Job save(Job job);

    List<Job> findAllByStatus(JobStatus status);

    Job findJobById(Long jobId);
}