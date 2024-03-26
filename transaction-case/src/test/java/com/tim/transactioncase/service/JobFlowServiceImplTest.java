package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JobFlowServiceImplTest {

    @InjectMocks
    private JobFlowService jobFlowServiceImpl;

    @Mock
    private OrderService orderServiceImpl;

    @Mock
    private ShipmentService shipmentServiceImpl;

    @Mock
    private JobService jobServiceImpl;

    @Mock
    private DriverService driverServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateJobFlow() {
        Order order = new Order();
        Driver driver = new Driver();

        Job job = jobFlowServiceImpl.createJobFlow(Arrays.asList(order), driver, Arrays.asList("DetailInfo"));
        verify(orderServiceImpl, times(1)).createOrderFlow(any(), anyList());
        verify(jobServiceImpl, times(1)).createJob("JobInfo", anyList(), eq(JobStatus.IN_PROGRESS), anyList());
        assertEquals(job, jobServiceImpl.createJob("JobInfo", Arrays.asList(order), JobStatus.IN_PROGRESS, Arrays.asList(new Shipment())));
    }

    @Test
    public void testUpdateJobStatusNormalFlow() {
        jobFlowServiceImpl.updateJobStatusNormalFlow(1L, JobStatus.COMPLETED);
        verify(jobServiceImpl, times(1)).findJobById(1L);
        verify(jobServiceImpl, times(1)).save(any(Job.class));
    }

    @Test
    public void testCreateJobFlowV2() {
        Order order = new Order();
        Driver driver = new Driver();

        Job job = jobFlowServiceImpl.createJobFlowV2(Arrays.asList(order), driver, Arrays.asList("DetailInfo"));
        verify(orderServiceImpl, times(1)).createOrderFlow(any(), anyList());
        verify(jobServiceImpl, times(1)).createJob("JobInfo", anyList(), eq(JobStatus.IN_PROGRESS), anyList());
        assertEquals(job, jobServiceImpl.createJob("JobInfo", Arrays.asList(order), JobStatus.IN_PROGRESS, Arrays.asList(new Shipment())));
    }

    @Test
    public void testUpdateJobStatusV2() {
        jobFlowServiceImpl.updateJobStatusV2(1L, JobStatus.COMPLETED);
        verify(jobServiceImpl, times(1)).findJobById(1L);
        verify(jobServiceImpl, times(1)).save(any(Job.class));
    }

    @Test
    public void testFindOpenJobs() {
        List<Job> jobs = jobFlowServiceImpl.findOpenJobs();
        verify(jobServiceImpl, times(1)).findAllByStatus(JobStatus.PENDING);
        assertEquals(jobs, jobServiceImpl.findAllByStatus(JobStatus.PENDING));
    }

    @Test
    public void testAssignJobToDriver() {
        Driver driver = new Driver();
        Job job = jobFlowServiceImpl.assignJobToDriver(1L, driver);
        verify(jobServiceImpl, times(1)).findJobById(1L);
        verify(jobServiceImpl, times(1)).save(any(Job.class));
        assertEquals(job, jobServiceImpl.save(new Job()));
    }
}
