package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.*;
import com.tim.transactioncase.service.DriverService;
import com.tim.transactioncase.service.JobFlowService;
import com.tim.transactioncase.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobFlowServiceImpl implements JobFlowService {
    private final OrderServiceImpl orderServiceImpl;

    private final ShipmentServiceImpl shipmentServiceImpl;

    private final JobService jobServiceImpl;

    private final DriverService driverServiceImpl;

    @Autowired
    public JobFlowServiceImpl(OrderServiceImpl orderServiceImpl, ShipmentServiceImpl shipmentServiceImpl, JobService jobServiceImpl, DriverService driverServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
        this.shipmentServiceImpl = shipmentServiceImpl;
        this.jobServiceImpl = jobServiceImpl;
        this.driverServiceImpl = driverServiceImpl;
    }

    public Job createJobFlow(List<Order> orderList, Long driverId, List<String> detailInfos) {
        Driver driver = driverServiceImpl.findDriverById(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist with id :" + driverId);
        }

        List<Shipment> shipments = orderList.stream()
                .map(order -> shipmentServiceImpl.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT))
                .collect(Collectors.toList());

        Order order = orderServiceImpl.createOrder("OrderInfo", detailInfos);
        Shipment shipment = shipmentServiceImpl.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT);

        order.setShipment(shipment);
        orderServiceImpl.save(order);

        List<Order> orders = new ArrayList<>(orderList);
        orders.add(order);
        shipments.add(shipment);

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Job createJobFlowV2(List<Order> orderList, Long driverId, List<String> detailInfos) {
        Driver driver = driverServiceImpl.findDriverById(driverId);

        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exist with id :" + driverId);
        }

        List<Shipment> shipments = orderList.stream()
                .map(order -> {
                    orderServiceImpl.createOrder(order.getOrderInfo(), detailInfos);
                    return shipmentServiceImpl.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT);
                })
                .collect(Collectors.toList());

        Job job = jobServiceImpl.createJob("JobInfo", orderList, JobStatus.IN_PROGRESS, shipments);
        driverServiceImpl.save(driver);

        return job;
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
