package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.CommonConstants;
import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.JobDataRepository;
import com.tim.transactioncase.repository.OrderRepository;
import com.tim.transactioncase.repository.ShipmentRepository;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.JobService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobDataRepository jobDataRepository;

    private final ShipmentRepository shipmentRepository;

    private final SequenceService sequenceService;

    private final OrderRepository orderRepository;

    @Autowired
    public JobServiceImpl(JobDataRepository jobDataRepository, ShipmentRepository shipmentRepository, SequenceService sequenceService, OrderRepository orderRepository) {
        this.jobDataRepository = jobDataRepository;
        this.shipmentRepository = shipmentRepository;
        this.sequenceService = sequenceService;
        this.orderRepository = orderRepository;
    }

    @SneakyThrows
    public Job createJob(List<Order> orders, JobStatus status, List<Shipment> shipments, String presetLine, Driver driver) {
        Job job = new Job();
        job.setStatus(status);
        job.setPId(sequenceService.getNextSequence(CommonConstants.JOB_SEQ_NAME).get());
        job.setPresetLine(presetLine);
        job.setDriver(driver);
        List<Long> pIds = orders.stream().map(Order::getPId).toList();

        if(!orderRepository.existsAllBypIdIn(pIds)){
            throw new RuntimeException();
        }
        job.setOrders(orders);
        return jobDataRepository.save(job);
    }

    @SneakyThrows
    public Job confirmJob(Long jobId){
        Job job = jobDataRepository.findBypId(jobId);
        if(ObjectUtils.isEmpty(job)){
            throw new RuntimeException();
        }
        if(!job.getStatus().equals(JobStatus.CREATED)){
            throw new RuntimeException();
        }

        job.setStatus(JobStatus.PLANNED);
        List<Shipment> shipments = job.getShipments();

        long shipmentId = sequenceService.getNextSequence(CommonConstants.SHIPMENT_SEQ_NAME,
                (long) shipments.size()).get() - shipments.size();
        for (Shipment item : shipments) {
            item.setPId(shipmentId++);
            item.setStatus(ShipmentStatus.IN_TRANSIT);
        }
        shipmentRepository.saveAll(shipments);
        return jobDataRepository.save(job);
    }

    @Override
    public List<Job> createJobFlow(List<CreateJobFlowRequest> createJobRequests) {
        return null;
    }

    public Job save(Job job) {
        return jobDataRepository.save(job);
    }

    public List<Job> findAllByStatus(JobStatus status ){
        return jobDataRepository.findAllByStatusIn(List.of(status));
    }

    public Job findJobById(Long jobId) {
        Optional<Job> job = jobDataRepository.findById(jobId);
        return job.orElse(null);
    }

    public boolean existAllByPIds(List<Long> jobId) {
        return jobDataRepository.existsAllBypIdIn(jobId);
    }
}
