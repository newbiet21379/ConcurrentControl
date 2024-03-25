package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JobFlowServiceTest {

    @InjectMocks
    private JobFlowService jobFlowService;

    @Mock
    private OrderService orderService;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private JobService jobService;

    @Mock
    private DriverService driverService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateJobFlow() {
        Order order = new Order();
        Driver driver = new Driver();

        Job job = jobFlowService.createJobFlow(Arrays.asList(order), driver, Arrays.asList("DetailInfo"));
        verify(orderService, times(1)).createOrderFlow(any(), anyList());
        verify(jobService, times(1)).createJob("JobInfo", anyList(), eq(JobStatus.IN_PROGRESS), anyList());
        assertEquals(job, jobService.createJob("JobInfo", Arrays.asList(order), JobStatus.IN_PROGRESS, Arrays.asList(new Shipment())));
    }

    @Test
    public void testUpdateJobStatusNormalFlow() {
        jobFlowService.updateJobStatusNormalFlow(1L, JobStatus.COMPLETED);
        verify(jobService, times(1)).findJobById(1L);
        verify(jobService, times(1)).save(any(Job.class));
    }

    @Test
    public void testCreateJobFlowV2() {
        Order order = new Order();
        Driver driver = new Driver();

        Job job = jobFlowService.createJobFlowV2(Arrays.asList(order), driver, Arrays.asList("DetailInfo"));
        verify(orderService, times(1)).createOrderFlow(any(), anyList());
        verify(jobService, times(1)).createJob("JobInfo", anyList(), eq(JobStatus.IN_PROGRESS), anyList());
        assertEquals(job, jobService.createJob("JobInfo", Arrays.asList(order), JobStatus.IN_PROGRESS, Arrays.asList(new Shipment())));
    }

    @Test
    public void testUpdateJobStatusV2() {
        jobFlowService.updateJobStatusV2(1L, JobStatus.COMPLETED);
        verify(jobService, times(1)).findJobById(1L);
        verify(jobService, times(1)).save(any(Job.class));
    }

    @Test
    public void testFindOpenJobs() {
        List<Job> jobs = jobFlowService.findOpenJobs();
        verify(jobService, times(1)).findAllByStatus(JobStatus.PENDING);
        assertEquals(jobs, jobService.findAllByStatus(JobStatus.PENDING));
    }

    @Test
    public void testAssignJobToDriver() {
        Driver driver = new Driver();
        Job job = jobFlowService.assignJobToDriver(1L, driver);
        verify(jobService, times(1)).findJobById(1L);
        verify(jobService, times(1)).save(any(Job.class));
        assertEquals(job, jobService.save(new Job()));
    }
}
