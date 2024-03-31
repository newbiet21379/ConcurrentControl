package com.tim.transactioncase.controller;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.JobFlowService;
import com.tim.transactioncase.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{version}/job")
public class JobFlowController {

    private final JobFlowService jobFlowServiceImpl;
    private final JobService jobService;
    @Autowired
    public JobFlowController(JobFlowService jobFlowService, JobService jobService) {
        this.jobFlowServiceImpl = jobFlowService;
        this.jobService = jobService;
    }

    @PostMapping("/create")
    public List<Job> createJobFlow(@RequestBody List<CreateJobFlowRequest> requests, @PathVariable("version") String version) {
        return switch (version) {
            case "v1" -> jobFlowServiceImpl.createJobFlow(requests);
            case "v2" -> jobFlowServiceImpl.createJobFlowTransaction(requests);
            default -> null;
        };
    }


    @PutMapping("/update/{id}/{status}")
    public void updateJobStatus(@PathVariable("id") Long jobId,
                                @PathVariable("status") String status,
                                @PathVariable("version") String version) {
        if(version.equals("v2")){
            jobFlowServiceImpl.updateJobStatusV2(jobId, JobStatus.valueOf(status));
        } else {
            jobFlowServiceImpl.updateJobStatusNormalFlow(jobId, JobStatus.valueOf(status));
        }
    }

    @GetMapping("/openJobs")
    public List<Job> findOpenJobs() {
        return jobFlowServiceImpl.findOpenJobs();
    }

    @PostMapping("/assign/{jobId}")
    public Job assignJobToDriver(@PathVariable("jobId") Long jobId,
                                 @RequestBody Driver driver,
                                 @PathVariable("version") String version) {
        if(version.equals("v2")){
            return jobFlowServiceImpl.assignJobToDriver(jobId, driver);
        }
        return null;
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Job> getJobById(@PathVariable Long jobId) {
        Job job = jobService.findJobById(jobId);
        return ResponseEntity.ok(job);
    }
}
