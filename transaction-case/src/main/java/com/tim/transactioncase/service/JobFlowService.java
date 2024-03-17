package com.tim.transactioncase.service;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public JobFlowService(OrderService orderService, ShipmentService shipmentService, JobService jobService, DriverService driverService, VehicleService vehicleService) {
        this.orderService = orderService;
        this.shipmentService = shipmentService;
        this.jobService = jobService;
        this.driverService = driverService;
    }

    public Job createJobFlow(List<Order> orderList, Driver driver, List<String> detailInfos) {
        List<Shipment> shipments = orderList.
                stream().map(order ->
                        shipmentService.createShipment("ShipmentInfo", order, driver))
                .collect(Collectors.toList());

        Order order = orderService.createOrder("OrderInfo", detailInfos);

        Shipment shipment = shipmentService.createShipment("ShipmentInfo", order, driver);
        order.setShipment(shipment);
        orderService.save(order);

        List<Order> orders = new ArrayList<>(orderList);
        orders.add(order);

        shipments.add(shipment);

        Job job = jobService.createJob("JobInfo", orders, "InProgress", shipments);

        driver.getJobs().add(job);
        driverService.save(driver);

        return job;
    }

    public void updateJobStatus(Long jobId, String status) {
        Job job = jobService.findJobById(jobId);
        job.setStatus(JobStatus.valueOf(status));

        if (status.equals("Finish")) {
            for (Shipment shipment : job.getShipments()) {
                shipmentService.updateShipmentInfo(shipment.getId(), "Delivered");
            }
        }

        jobService.save(job);
    }
}
