package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.request.CreateJobFlowRequest;

import java.util.List;

public interface JobFlowService {

    Job createJobFlow(List<CreateJobFlowRequest> requests);

    void updateJobStatusNormalFlow(Long jobId, JobStatus status);

    Job createJobFlowV2(List<CreateJobFlowRequest> jobFlowRequests);

    void updateJobStatusV2(Long jobId, JobStatus status);

    List<Job> findOpenJobs();

    Job assignJobToDriver(Long jobId, Driver driver);

}