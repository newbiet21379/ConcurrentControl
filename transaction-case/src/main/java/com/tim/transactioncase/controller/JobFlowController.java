package com.tim.transactioncase.controller;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.JobFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobFlowController {

    private final JobFlowService jobFlowServiceImpl;

    @Autowired
    public JobFlowController(JobFlowService jobFlowServiceImpl) {
        this.jobFlowServiceImpl = jobFlowServiceImpl;
    }

    @PostMapping("/create")
    public Job createJobFlow(@RequestBody CreateJobFlowRequest request) {
        return jobFlowServiceImpl.createJobFlow(request.getOrderList(), request.getDriver(), request.getDetailInfos());
    }

    @PutMapping("/update/{id}/{status}")
    public void updateJobStatusNormalFlow(@PathVariable("id") Long jobId, @PathVariable("status") String status) {
        jobFlowServiceImpl.updateJobStatusNormalFlow(jobId, JobStatus.valueOf(status));
    }

    @GetMapping("/openJobs")
    public List<Job> findOpenJobs() {
        return jobFlowServiceImpl.findOpenJobs();
    }

    @PostMapping("/assign/{jobId}")
    public Job assignJobToDriver(@PathVariable("jobId") Long jobId, @RequestBody Driver driver) {
        return jobFlowServiceImpl.assignJobToDriver(jobId, driver);
    }
}
