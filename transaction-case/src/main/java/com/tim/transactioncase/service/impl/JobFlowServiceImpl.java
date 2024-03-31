package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.OrderStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.common.TransactionWrapper;
import com.tim.transactioncase.exception.ResourceNotFoundException;
import com.tim.transactioncase.model.*;
import com.tim.transactioncase.repository.OrderExecuteRepository;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.*;
import com.tim.transactioncase.utils.CreateJobFlowMapper;
import com.tim.transactioncase.utils.ShipmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobFlowServiceImpl implements JobFlowService {

    private Integer countPresetLine = 0;
    private final OrderService orderServiceImpl;

    private final ShipmentService shipmentServiceImpl;

    private final JobService jobServiceImpl;

    private final DriverService driverServiceImpl;

    private final TransactionWrapper transactionWrapper;

    private final OrderExecuteRepository orderExecuteRepository;

    public JobFlowServiceImpl(OrderService orderServiceImpl, ShipmentService shipmentServiceImpl, JobService jobServiceImpl, DriverService driverServiceImpl, TransactionWrapper transactionWrapper, OrderExecuteRepository orderExecuteRepository) {
        this.orderServiceImpl = orderServiceImpl;
        this.shipmentServiceImpl = shipmentServiceImpl;
        this.jobServiceImpl = jobServiceImpl;
        this.driverServiceImpl = driverServiceImpl;
        this.transactionWrapper = transactionWrapper;
        this.orderExecuteRepository = orderExecuteRepository;
    }

    public Integer getCountPresetLine(){
        return ++countPresetLine;
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

    public List<Job> createJobFlow(List<CreateJobFlowRequest> createJobRequests) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(createJobRequests);
        List<Driver> drivers = getDriversFromList(CreateJobFlowMapper.toDriverIdList(createJobRequests));
        if(ObjectUtils.isEmpty(drivers)) return null;
        orders = orderServiceImpl.saveAll(orders);
        Map<String, List<Order>> presetLineMap = new HashMap<>();
        Map<String, Driver> lineDriver = new HashMap<>();

        for (Order order : orders) {
            String presetLine = ObjectUtils.isEmpty(order.getPresetLine()) ? String.valueOf(getCountPresetLine()) : order.getPresetLine();
            if (ObjectUtils.isEmpty(presetLineMap.get(presetLine))) {
                List<Order> orderList = new ArrayList<>();
                orderList.add(order);
                presetLineMap.put(presetLine, orderList);
            } else {
                presetLineMap.get(presetLine).add(order);
            }
        }

        for (CreateJobFlowRequest jobFlowRequest : createJobRequests) {
            String presetLine = ObjectUtils.isEmpty(jobFlowRequest.getPresetLine()) ? String.valueOf(getCountPresetLine()) : jobFlowRequest.getPresetLine();
            Driver driver = getDriverFromJobRequest(jobFlowRequest);
            lineDriver.put(presetLine, driver);
        }
        List<Job> jobs = new ArrayList<>(presetLineMap.size());

        presetLineMap.forEach((key, value) -> {
            Driver driver = lineDriver.get(key);

            Job job = jobServiceImpl.createJob(value, JobStatus.CREATED, new ArrayList<>(), key, driver);

            List<Shipment> shipments = new ArrayList<>();
            for (Order order : value) {
                order.setOrderStatus(OrderStatus.PENDING);
                order.setJob(job);
                Shipment shipment = ShipmentMapper.toShipment(order, driver, ShipmentStatus.PENDING);
                shipment.setJob(job);
                order.setShipment(shipment);
                shipments.add(shipmentServiceImpl.save(shipment));
            }


            int shipmentSeq = 1;
            for (Shipment shipment : shipments) {
                shipment.setSequence(shipmentSeq++);
                shipmentServiceImpl.save(shipment);
            }
            job.setShipments(shipments);
            driver.getJobs().add(job);
            jobs.add(jobServiceImpl.confirmJob(job));
        });
        return jobs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Job> createJobFlowTransaction(List<CreateJobFlowRequest> createJobRequests) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(createJobRequests);
        List<Driver> drivers = getDriversFromList(CreateJobFlowMapper.toDriverIdList(createJobRequests));
        if(ObjectUtils.isEmpty(drivers)) return null;
        orders = orderServiceImpl.saveAll(orders);
        Map<String, List<Order>> presetLineMap = new HashMap<>();
        Map<String, Driver> lineDriver = new HashMap<>();

        for (Order order : orders) {
            String presetLine = ObjectUtils.isEmpty(order.getPresetLine()) ? String.valueOf(getCountPresetLine()) : order.getPresetLine();
            if (ObjectUtils.isEmpty(presetLineMap.get(presetLine))) {
                List<Order> orderList = new ArrayList<>();
                orderList.add(order);
                presetLineMap.put(presetLine, orderList);
            } else {
                presetLineMap.get(presetLine).add(order);
            }
        }

        for (CreateJobFlowRequest jobFlowRequest : createJobRequests) {
            String presetLine = ObjectUtils.isEmpty(jobFlowRequest.getPresetLine()) ? String.valueOf(getCountPresetLine()) : jobFlowRequest.getPresetLine();
            Driver driver = getDriverFromJobRequest(jobFlowRequest);
            lineDriver.put(presetLine, driver);
        }

        List<Job> jobs = new ArrayList<>(presetLineMap.size());

        presetLineMap.forEach((key, value) -> {
            Driver driver = lineDriver.get(key);

            Job job = jobServiceImpl.createJob(value, JobStatus.CREATED, new ArrayList<>(), key, driver);

            List<Shipment> shipments = new ArrayList<>();
            for (Order order : value) {
                order.setOrderStatus(OrderStatus.PENDING);
                order.setJob(job);
                Shipment shipment = ShipmentMapper.toShipment(order, driver, ShipmentStatus.PENDING);
                shipment.setJob(job);
                order.setShipment(shipment);
                shipments.add(shipmentServiceImpl.save(shipment));
            }


            int shipmentSeq = 1;
            for (Shipment shipment : shipments) {
                shipment.setSequence(shipmentSeq++);
                shipmentServiceImpl.save(shipment);
            }
            job.setShipments(shipments);
            driver.getJobs().add(job);
            jobs.add(jobServiceImpl.confirmJob(job));
        });

        return jobs;
    }

    private Driver getDriverFromJobRequest(CreateJobFlowRequest createJobFlowRequest){
        return driverServiceImpl.findDriverById(createJobFlowRequest.getDriverId());
    }

    private List<Job> createJobsPerDriver(List<Driver> drivers) {
        // Create Job for each driver with PENDING status
        return drivers.stream().map(driver -> {
            Job job = new Job();
            job.setStatus(JobStatus.CREATED);
            job.setDriver(driver);
            return jobServiceImpl.save(job);
        }).collect(Collectors.toList());
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
