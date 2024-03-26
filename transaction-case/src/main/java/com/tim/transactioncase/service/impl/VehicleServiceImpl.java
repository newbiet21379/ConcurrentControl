package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.repository.VehicleRepository;
import com.tim.transactioncase.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle createVehicle(String model) {
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(model);
        return vehicleRepository.save(vehicle);
    }
}
