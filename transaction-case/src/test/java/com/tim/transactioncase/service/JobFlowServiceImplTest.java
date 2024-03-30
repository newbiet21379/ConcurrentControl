package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.common.TransactionWrapper;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.impl.*;
import com.tim.transactioncase.utils.CreateJobFlowMapper;
import com.tim.transactioncase.utils.ShipmentMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class JobFlowServiceImplTest {

    @InjectMocks
    private JobFlowServiceImpl jobFlowServiceImpl;

    @Mock
    private OrderServiceImpl orderServiceImpl;

    @Mock
    private JobServiceImpl jobServiceImpl;

    @Mock
    private DriverServiceImpl driverServiceImpl;

    @Mock
    private ShipmentServiceImpl shipmentService;

    @Mock
    private TransactionWrapper transactionWrapper;

    private Job job;
    private Driver driver;

    @BeforeEach
    public void setup() {
        // Init setup for Unit Test
        job = new Job();
        driver = new Driver();
        driver.setId(1L);
        Shipment shipment = this.generateShipment();
        List<Job> mockJobs = new ArrayList<>();
        List<Shipment> mockShipments = new ArrayList<>();
        mockJobs.add(job);
        mockShipments.add(shipment);
        driver.setJobs(mockJobs);
        job.setShipments(mockShipments);
        job.setDriver(driver);
        job.setStatus(JobStatus.IN_PROGRESS);

        MockitoAnnotations.openMocks(this);
        when(jobServiceImpl.findJobById(anyLong())).thenReturn(job);
        when(jobServiceImpl.save(any())).thenReturn(job);
        when(shipmentService.createShipment(anyString(), any(), any(), any())).thenReturn(new Shipment());
        when(orderServiceImpl.createOrder(anyString(), anyList())).thenReturn(new Order());
        when(orderServiceImpl.save(any())).thenReturn(new Order());
        when(driverServiceImpl.findDriverById(1L)).thenReturn(driver);
        doNothing().when(driverServiceImpl).save(any());
    }

    public static List<CreateJobFlowRequest> getMockData() {
        return Collections.singletonList(new CreateJobFlowRequest(
                (long)1,
                (long)1,
                "Order info",
                "Detail info" ,
                "Shipment info",
                JobStatus.COMPLETED,
                ShipmentStatus.DELIVERED
        ));
    }

    @NotNull
    private Shipment generateShipment() {
        Shipment shipment = new Shipment();
        shipment.setDriver(driver);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setShipmentInfo("ShipmentInfo");
        shipment.setId(1L);
        return shipment;
    }

    @Test
    public void testUpdateJobStatusNormalFlow() {
        jobFlowServiceImpl.updateJobStatusNormalFlow(1L, JobStatus.COMPLETED);
        verify(jobServiceImpl, times(1)).findJobById(1L);
        verify(jobServiceImpl, times(1)).save(any(Job.class));
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
        Job job = jobFlowServiceImpl.assignJobToDriver(1L, driver);
        verify(jobServiceImpl, times(1)).findJobById(1L);
        verify(jobServiceImpl, times(1)).save(any(Job.class));
        assertEquals(job, jobServiceImpl.save(new Job()));
    }

    @Test
    void shouldUpdateJobStatusNormalFlow() {
        // Act
        jobFlowServiceImpl.updateJobStatusNormalFlow(1L, JobStatus.COMPLETED);

        // Assert
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

    @Test
    void shouldFindOpenJobs() {
        // Arrange
        List<Job> expectedJobs = new ArrayList<>();
        given(jobServiceImpl.findAllByStatus(JobStatus.PENDING)).willReturn(expectedJobs);

        // Act
        List<Job> actualJobs = jobFlowServiceImpl.findOpenJobs();

        // Assert
        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    void shouldAssignJobToDriver() {
        // Act
        Job returnedJob = jobFlowServiceImpl.assignJobToDriver(1L, driver);

        // Assert
        assertEquals(driver, returnedJob.getDriver());
        assertEquals(JobStatus.ASSIGNED, returnedJob.getStatus());
    }
}
