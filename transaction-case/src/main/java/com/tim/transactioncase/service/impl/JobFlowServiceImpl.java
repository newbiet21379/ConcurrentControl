package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.common.TransactionWrapper;
import com.tim.transactioncase.exception.ResourceNotFoundException;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.*;
import com.tim.transactioncase.utils.CreateJobFlowMapper;
import com.tim.transactioncase.utils.ShipmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.concurrent.FutureUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class JobFlowServiceImpl implements JobFlowService {
    private final OrderService orderServiceImpl;

    private final ShipmentService shipmentServiceImpl;

    private final JobService jobServiceImpl;

    private final DriverService driverServiceImpl;

    private final TransactionWrapper transactionWrapper;

    public JobFlowServiceImpl(OrderService orderServiceImpl, ShipmentService shipmentServiceImpl, JobService jobServiceImpl, DriverService driverServiceImpl, TransactionWrapper transactionWrapper) {
        this.orderServiceImpl = orderServiceImpl;
        this.shipmentServiceImpl = shipmentServiceImpl;
        this.jobServiceImpl = jobServiceImpl;
        this.driverServiceImpl = driverServiceImpl;
        this.transactionWrapper = transactionWrapper;
    }

    public Job createJobFlow(List<CreateJobFlowRequest> createJobRequests) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(createJobRequests);
        List<Driver> drivers = getDriversFromList(CreateJobFlowMapper.toDriverIdList(createJobRequests));
        List<Shipment> shipments = ShipmentMapper.toShipmentList(orders, drivers, ShipmentStatus.IN_TRANSIT);
        if (ObjectUtils.isEmpty(drivers)) {
            throw new ResourceNotFoundException("Driver does not exist");
        }
        Driver driver = driverServiceImpl.findDriverById(drivers.get(0).getId());


        Job job = jobServiceImpl.createJob("JobInfo", orders, JobStatus.IN_PROGRESS, shipments);
        if(!driver.getJobs().isEmpty()){
            driver.getJobs().add(job);
            driverServiceImpl.save(driver);
        }
        return job;
    }

    public void updateJobStatusNormalFlow(Long jobId, JobStatus status) {
        updateStatusJob(jobId, status);
    }

    private void updateStatusJob(Long jobId, JobStatus status) {
        Job job = jobServiceImpl.findJobById(jobId);
        // Prevent status change if it's already COMPLETED
        if (!job.getStatus().equals(JobStatus.COMPLETED)) {
            job.setStatus(status);

            if (status.equals(JobStatus.COMPLETED)) {
                if (!job.getShipments().isEmpty()) {
                    for (Shipment shipment : job.getShipments()) {
                        shipmentServiceImpl.updateShipmentInfo(shipment.getId(), "Delivered", ShipmentStatus.DELIVERED);
                    }
                }
            }

            jobServiceImpl.save(job);
        } else {
            throw new IllegalStateException("Cannot change status of a COMPLETED job");
        }
    }

    @Transactional
    public Job createJobFlowV2(List<CreateJobFlowRequest> createJobRequests) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(createJobRequests);
        List<Driver> drivers = getDriversFromList(CreateJobFlowMapper.toDriverIdList(createJobRequests));
        List<Shipment> shipments = ShipmentMapper.toShipmentList(orders, drivers, ShipmentStatus.IN_TRANSIT);
        // Separate transaction
        transactionWrapper.doInTransaction(() -> {
            orderServiceImpl.saveAll(orders);
            driverServiceImpl.saveAll(drivers);
            shipments.forEach(shipment ->
                    FutureUtils.callAsync(() ->
                            shipmentServiceImpl.createShipment(
                                    shipment.getShipmentInfo()
                                    , shipment.getOrder()
                                    , shipment.getDriver()
                                    , shipment.getStatus())
                    )
            );
        });

        return jobServiceImpl.createJob("JobInfo"
                , orders
                , createJobRequests.get(0).getJobStatus()
                , shipments);
    }

    private Driver getDriverFromService(Long driverId) {
        Driver driver = driverServiceImpl.findDriverById(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist with id :" + driverId);
        }
        return driver;
    }

    public List<Driver> getDriversFromList(List<Long> driverIds) {
        if (ObjectUtils.isEmpty(driverIds)) {
            throw new ResourceNotFoundException();
        }
        return driverServiceImpl.findDriversByIds(driverIds);
    }

    @Transactional
    public void updateJobStatusV2(Long jobId, JobStatus status) {
        updateStatusJob(jobId, status);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<Job> findOpenJobs() {
        return jobServiceImpl.findAllByStatus(JobStatus.PENDING);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Job assignJobToDriver(Long jobId, Driver driver) {
        Job job = jobServiceImpl.findJobById(jobId);

        //Check if job status is COMPLETE before assigning to a driver
        if (job.getStatus().equals(JobStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot assign a COMPLETED job");
        }

        job.setDriver(driver);
        job.setStatus(JobStatus.ASSIGNED);

        return jobServiceImpl.save(job);
    }
}
