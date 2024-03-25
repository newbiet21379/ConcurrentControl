package com.tim.transactioncase.controller;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.service.JobFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobFlowController {

    private final JobFlowService jobFlowService;

    @Autowired
    public JobFlowController(JobFlowService jobFlowService) {
        this.jobFlowService = jobFlowService;
    }

    @PostMapping("/create")
    public Job createJobFlow(@RequestBody List<Order> orderList, @RequestBody Driver driver, @RequestBody List<String> detailInfos) {
        return jobFlowService.createJobFlow(orderList, driver, detailInfos);
    }

    @PutMapping("/update/{id}/{status}")
    public void updateJobStatusNormalFlow(@PathVariable("id") Long jobId, @PathVariable("status") String status) {
        jobFlowService.updateJobStatusNormalFlow(jobId, JobStatus.valueOf(status));
    }

    @GetMapping("/openJobs")
    public List<Job> findOpenJobs() {
        return jobFlowService.findOpenJobs();
    }

    @PostMapping("/assign/{jobId}")
    public Job assignJobToDriver(@PathVariable("jobId") Long jobId, @RequestBody Driver driver) {
        return jobFlowService.assignJobToDriver(jobId, driver);
    }
}
