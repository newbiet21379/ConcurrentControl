package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.request.JobBatchRequest;

import java.util.List;

public interface JobFlowService {

    List<Job> createJobFlow(JobBatchRequest request);
    List<Job> createJobFlowTransaction(JobBatchRequest request);

    List<Job> createJobFlowWithSeparateTransaction(JobBatchRequest request);
    void updateJobStatusNormalFlow(Long jobId, JobStatus status);

    void updateJobStatusV2(Long jobId, JobStatus status);

    List<Job> findOpenJobs();

    Job assignJobToDriver(Long jobId, Driver driver);
}