package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobFlowService {
    private final OrderService orderService;

    private final ShipmentService shipmentService;

    private final JobService jobService;

    private final DriverService driverService;

    @Autowired
    public JobFlowService(OrderService orderService, ShipmentService shipmentService, JobService jobService, DriverService driverService) {
        this.orderService = orderService;
        this.shipmentService = shipmentService;
        this.jobService = jobService;
        this.driverService = driverService;
    }

    public Job createJobFlow(List<Order> orderList, Driver driver, List<String> detailInfos) {
        List<Shipment> shipments = orderList.stream()
                .map(order -> shipmentService.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT))
                .collect(Collectors.toList());

        Order order = orderService.createOrder("OrderInfo", detailInfos);
        Shipment shipment = shipmentService.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT);

        order.setShipment(shipment);
        orderService.save(order);

        List<Order> orders = new ArrayList<>(orderList);
        orders.add(order);
        shipments.add(shipment);

        Job job = jobService.createJob("JobInfo", orders, JobStatus.IN_PROGRESS, shipments);
        driver.getJobs().add(job);
        driverService.save(driver);

        return job;
    }

    public void updateJobStatusNormalFlow(Long jobId, JobStatus status) {
        updateStatusJob(jobId, status);
    }

    private void updateStatusJob(Long jobId, JobStatus status) {
        Job job = jobService.findJobById(jobId);
        job.setStatus(status);

        if (status.equals(JobStatus.COMPLETED)) {
            for (Shipment shipment : job.getShipments()) {
                shipmentService.updateShipmentInfo(shipment.getId(), "Delivered", ShipmentStatus.DELIVERED);
            }
        }

        jobService.save(job);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Job createJobFlowV2(List<Order> orderList, Driver driver, List<String> detailInfos) {
        List<Shipment> shipments = orderList.stream()
                .map(order -> shipmentService.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT))
                .collect(Collectors.toList());

        Order order = orderService.createOrder("OrderInfo", detailInfos);
        Shipment shipment = shipmentService.createShipment("ShipmentInfo", order, driver, ShipmentStatus.IN_TRANSIT);

        order.setShipment(shipment);
        orderService.save(order);

        List<Order> orders = new ArrayList<>(orderList);
        orders.add(order);
        shipments.add(shipment);

        Job job = jobService.createJob("JobInfo", orders, JobStatus.IN_PROGRESS, shipments);
        driver.getJobs().add(job);
        driverService.save(driver);

        return job;
    }

    @Transactional
    public void updateJobStatusV2(Long jobId, JobStatus status) {
        updateStatusJob(jobId, status);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<Job> findOpenJobs() {
        return jobService.findAllByStatus(JobStatus.PENDING);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Job assignJobToDriver(Long jobId, Driver driver) {
        Job job = jobService.findJobById(jobId);
        job.setDriver(driver);
        job.setStatus(JobStatus.ASSIGNED);
        return jobService.save(job);
    }
}
