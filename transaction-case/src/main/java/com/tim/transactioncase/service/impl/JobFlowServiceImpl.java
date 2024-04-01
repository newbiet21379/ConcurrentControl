package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.*;
import com.tim.transactioncase.exception.ResourceNotFoundException;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.OrderExecuteRepository;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.request.JobBatchRequest;
import com.tim.transactioncase.service.*;
import com.tim.transactioncase.utils.CreateJobFlowMapper;
import com.tim.transactioncase.utils.ShipmentMapper;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class JobFlowServiceImpl implements JobFlowService {

    private Integer countPresetLine = 0;
    private final OrderService orderServiceImpl;

    private final ShipmentService shipmentServiceImpl;

    private final JobService jobServiceImpl;

    private final DriverService driverServiceImpl;

    private final TransactionWrapper transactionWrapper;

    private final SequenceService sequenceService;

    public JobFlowServiceImpl(OrderService orderServiceImpl, ShipmentService shipmentServiceImpl, JobService jobServiceImpl, DriverService driverServiceImpl, TransactionWrapper transactionWrapper, OrderExecuteRepository orderExecuteRepository, SequenceService sequenceService, RedissonClient redissonClient) {
        this.orderServiceImpl = orderServiceImpl;
        this.shipmentServiceImpl = shipmentServiceImpl;
        this.jobServiceImpl = jobServiceImpl;
        this.driverServiceImpl = driverServiceImpl;
        this.transactionWrapper = transactionWrapper;
        this.sequenceService = sequenceService;
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

    @SneakyThrows
    public List<Job> createJobFlow(List<CreateJobFlowRequest> createJobRequests) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(createJobRequests);
        long orderId = sequenceService.getNextSequence(CommonConstants.ORDER_SEQ_NAME,
                (long) orders.size()).get() - orders.size();
        for (Order item : orders) {
            item.setPId(orderId++);
        }

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
            Long jobId = planJobWithDriver(key, value, lineDriver);
            jobs.add(jobServiceImpl.confirmJob(jobId));
        });
        return jobs;
    }

    @SneakyThrows
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Job> createJobFlowTransaction(JobBatchRequest request) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(request.getJobFlowRequests());
        long orderId = sequenceService.getNextSequence(CommonConstants.ORDER_SEQ_NAME,
                (long) orders.size()).get() - orders.size();
        long orderExecuteId = sequenceService.getNextSequence(CommonConstants.ORDER_EXECUTE_SEQ_NAME,
                (long) orders.size()).get() - orders.size();
        for (Order item : orders) {
            item.setPId(orderId++);
            item.getOrderExecute().setPId(orderExecuteId++);
        }
        List<Driver> drivers = getDriversFromList(CreateJobFlowMapper.toDriverIdList(request.getJobFlowRequests()));
        if(ObjectUtils.isEmpty(drivers)) return null;
        orderServiceImpl.saveAll(orders);
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

        for (CreateJobFlowRequest jobFlowRequest : request.getJobFlowRequests()) {
            String presetLine = ObjectUtils.isEmpty(jobFlowRequest.getPresetLine()) ? String.valueOf(getCountPresetLine()) : jobFlowRequest.getPresetLine();
            Driver driver = getDriverFromJobRequest(jobFlowRequest);
            lineDriver.put(presetLine, driver);
        }

        List<Job> jobs = new ArrayList<>(presetLineMap.size());

        presetLineMap.forEach((key, value) -> {
            if(request.isAsync()){
                CompletableFuture.supplyAsync(() -> {
                    Long jobId = planJobWithDriver(key, value, lineDriver);
                    jobs.add(jobServiceImpl.confirmJob(jobId));
                    return null;
                });
            }else{
                Long jobId = planJobWithDriver(key, value, lineDriver);
                jobs.add(jobServiceImpl.confirmJob(jobId));
            }
        });

        return jobs;
    }

    @SneakyThrows
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Job> createJobFlowWithSeparateTransaction(JobBatchRequest request) {
        List<Order> orders = CreateJobFlowMapper.toOrderList(request.getJobFlowRequests());
        List<Order> finalOrders = orders;
        orders = transactionWrapper.doInNewTransaction(() -> {
            long orderId;
            try {
                orderId = sequenceService.getNextSequence(CommonConstants.ORDER_SEQ_NAME,
                        (long) finalOrders.size()).get() - finalOrders.size();

                long orderExecuteId = sequenceService.getNextSequence(CommonConstants.ORDER_EXECUTE_SEQ_NAME,
                        (long) finalOrders.size()).get() - finalOrders.size();
                for (Order item : finalOrders) {
                    item.setPId(orderId++);
                    item.getOrderExecute().setPId(orderExecuteId++);
                }
                List<Driver> drivers = getDriversFromList(CreateJobFlowMapper.toDriverIdList(request.getJobFlowRequests()));
                if(ObjectUtils.isEmpty(drivers)) return null;
                orderServiceImpl.saveAll(finalOrders);
                return finalOrders;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

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

        for (CreateJobFlowRequest jobFlowRequest : request.getJobFlowRequests()) {
            String presetLine = ObjectUtils.isEmpty(jobFlowRequest.getPresetLine()) ? String.valueOf(getCountPresetLine()) : jobFlowRequest.getPresetLine();
            Driver driver = getDriverFromJobRequest(jobFlowRequest);
            lineDriver.put(presetLine, driver);
        }

        List<Job> jobs = new ArrayList<>(presetLineMap.size());

        presetLineMap.forEach((key, value) -> {
            if(request.isAsync()){
                CompletableFuture.supplyAsync(() -> {
                    Long jobId = transactionWrapper.doInSameTransaction(() -> planJobWithDriver(key, value, lineDriver));
                    Job job = transactionWrapper.doInSameTransaction(() -> jobServiceImpl.confirmJob(jobId));
                    jobs.add(job);
                    return null;
                });
            }else{
                Long jobId = planJobWithDriver(key, value, lineDriver);
                jobs.add(jobServiceImpl.confirmJob(jobId));
            }
        });

        return jobs;
    }

    private Long planJobWithDriver(String key, List<Order> value, Map<String, Driver> lineDriver) {
        Driver driver = lineDriver.get(key);

        Job job = jobServiceImpl.createJob(value, JobStatus.CREATED, new ArrayList<>(), key, driver);

        List<Shipment> shipments = new ArrayList<>();
        if(!jobServiceImpl.existAllByPIds(Collections.singletonList(job.getPId()))){
            throw new RuntimeException();
        }
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
        driverServiceImpl.save(driver);
        return job.getPId();
    }

    private Driver getDriverFromJobRequest(CreateJobFlowRequest createJobFlowRequest){
        return driverServiceImpl.findDriverById(createJobFlowRequest.getDriverId());
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
