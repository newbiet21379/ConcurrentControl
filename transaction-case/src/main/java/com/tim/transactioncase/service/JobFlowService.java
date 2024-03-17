package com.tim.transactioncase.service;

import com.tim.transactioncase.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobFlowService {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private JobService jobService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private VehicleService vehicleService;

    public Job createJobFlow(List<Order> orderList, Driver driver, List<String> detailInfos) {
        // Create Shipment for each Order
        List<Shipment> shipments = orderList.stream().map(order -> shipmentService.createShipment("ShipmentInfo", order, driver)).collect(Collectors.toList());

// Create Vehicle (but it's not used in the following lines)
        Vehicle vehicle = vehicleService.createVehicle("Vehicle Model");

// Create Order with orderInfo and list of details
        Order order = orderService.createOrder("OrderInfo", detailInfos);

// Create and link Shipment to the newly created Order
        Shipment shipment = shipmentService.createShipment("ShipmentInfo", order, driver);
        order.setShipment(shipment);
        orderService.save(order);

// Append newly created order to list of Orders
        List<Order> orders = new ArrayList<>(orderList); // this 'orderList' should be list of already existent orders
        orders.add(order);

// Add new Shipment to the list of Shipments
        shipments.add(shipment);

// Create a Job with the given orders and shipments, then set its status
        Job job = jobService.createJob("JobInfo", orders, "InProgress", shipments);

// Assign the Job to the Driver
        driver.getJobs().add(job);
        driverService.save(driver);

        return job;
    }

    public void updateJobStatus(Long jobId, String status) {
        Job job = jobService.findJobById(jobId);
        job.setStatus(status);

        // Update all shipments of job if job is finished
        if (status.equals("Finish")) {
            for (Shipment shipment : job.getShipments()) {
                shipmentService.updateShipmentInfo(shipment.getId(), "Delivered");
            }
        }

        jobService.save(job);
    }
}
