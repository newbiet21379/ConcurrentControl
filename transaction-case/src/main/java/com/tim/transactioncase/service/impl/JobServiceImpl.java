package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.Shipment;
import com.tim.transactioncase.repository.JobDataRepository;
import com.tim.transactioncase.repository.ShipmentRepository;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobDataRepository jobDataRepository;

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public JobServiceImpl(JobDataRepository jobDataRepository, ShipmentRepository shipmentRepository) {
        this.jobDataRepository = jobDataRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Job createJob(List<Order> orders, JobStatus status, List<Shipment> shipments, String presetLine, Driver driver) {
        Job job = new Job();
        job.setStatus(status);
        job.setPresetLine(presetLine);
        job.setShipments(shipments);
        job.setDriver(driver);
        job.setOrders(orders);
        return jobDataRepository.save(job);
    }

    public Job confirmJob(Job job){
        if(!job.getStatus().equals(JobStatus.CREATED)){
            throw new RuntimeException();
        }

        job.setStatus(JobStatus.PLANNED);
        List<Shipment> shipments = job.getShipments();
        shipments.forEach(shipment -> shipment.setStatus(ShipmentStatus.IN_TRANSIT));
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
}
