package com.tim.transactioncase.service;

import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle createVehicle(String model) {
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(model);
        return vehicleRepository.save(vehicle);
    }
}
