package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.JobDataRepository;
import com.tim.transactioncase.repository.OrderRepository;
import com.tim.transactioncase.service.impl.*;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

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

    private Job job;
    private Driver driver;

    @BeforeEach
    public void setup() {
        job = new Job();
        driver = new Driver();
        Shipment shipment = new Shipment();

        List<Job> mockJobs = new ArrayList<>();
        List<Shipment> mockShipments = new ArrayList<>();
        mockJobs.add(job);
        mockShipments.add(shipment);
        driver.setJobs(mockJobs);
        job.setShipments(mockShipments);
        job.setDriver(driver);
        MockitoAnnotations.openMocks(this);
        when(jobServiceImpl.findJobById(anyLong())).thenReturn(job);
        when(jobServiceImpl.save(any())).thenReturn(job);
        when(shipmentService.createShipment(anyString(), any(), any(), any())).thenReturn(new Shipment());
        when(orderServiceImpl.createOrder(anyString(), anyList())).thenReturn(new Order());
        when(orderServiceImpl.save(any())).thenReturn(new Order());
        doNothing().when(driverServiceImpl).save(any());
    }

    @Test
    public void testCreateJobFlow() {
        Order order = new Order();

        Job job = jobFlowServiceImpl.createJobFlow(List.of(order), driver, List.of("DetailInfo"));
        verify(jobServiceImpl, times(1)).createJob(anyString(), anyList(), eq(JobStatus.IN_PROGRESS), anyList());
        assertEquals(job, jobServiceImpl.createJob("JobInfo", List.of(order), JobStatus.IN_PROGRESS, List.of(new Shipment())));
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

        Job job = jobFlowServiceImpl.createJobFlowV2(Arrays.asList(order), driver, Arrays.asList("DetailInfo"));
        verify(jobServiceImpl, times(1)).createJob(anyString(), anyList(), eq(JobStatus.IN_PROGRESS), anyList());
        assertEquals(job, jobServiceImpl.createJob("JobInfo", Arrays.asList(order), JobStatus.IN_PROGRESS, Arrays.asList(new Shipment())));
    }

    @Test
    void testCreateJobFlowV2WithException() {
        // Arrange
        Order order = new Order();
        RuntimeException runtimeException = new RuntimeException("Transaction exception");

        // Make createJob throw the RuntimeException when called
        doThrow(runtimeException).when(jobServiceImpl).createJob(anyString(), anyList(), eq(JobStatus.IN_PROGRESS), anyList());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> jobFlowServiceImpl.createJobFlowV2(List.of(order), driver, List.of("DetailInfo")));
        verify(jobServiceImpl, times(1)).createJob(anyString(), anyList(), eq(JobStatus.IN_PROGRESS), anyList());
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
    void shouldCreateJobFlow() {
        // Arrange
        List<Order> orderList = new ArrayList<>();
        List<String> detailInfos = new ArrayList<>();
        Job expectedJob = new Job();
        when(jobServiceImpl.createJob(anyString(), anyList(), any(JobStatus.class), anyList())).thenReturn(expectedJob);

        // Act
        Job actualJob = jobFlowServiceImpl.createJobFlow(orderList, driver, detailInfos);

        // Assert
        assertNotNull(actualJob);
        assertEquals(expectedJob, actualJob);
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
