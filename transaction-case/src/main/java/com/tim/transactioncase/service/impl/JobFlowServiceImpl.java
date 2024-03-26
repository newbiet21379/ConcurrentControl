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

    public Job createJobFlow(List<Order> orderList, Driver driver, List<String> detailInfos) {
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
        driver.getJobs().add(job);
        driverServiceImpl.save(driver);

        return job;
    }

    public void updateJobStatusNormalFlow(Long jobId, JobStatus status) {
        updateStatusJob(jobId, status);
    }

    private void updateStatusJob(Long jobId, JobStatus status) {
        Job job = jobServiceImpl.findJobById(jobId);
        job.setStatus(status);

        if (status.equals(JobStatus.COMPLETED)) {
            for (Shipment shipment : job.getShipments()) {
                shipmentServiceImpl.updateShipmentInfo(shipment.getId(), "Delivered", ShipmentStatus.DELIVERED);
            }
        }

        jobServiceImpl.save(job);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Job createJobFlowV2(List<Order> orderList, Driver driver, List<String> detailInfos) {
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
        driver.getJobs().add(job);
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
        job.setDriver(driver);
        job.setStatus(JobStatus.ASSIGNED);
        return jobServiceImpl.save(job);
    }
}
