package com.tim.transactioncase.service;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public Driver createDriver(String name, Vehicle vehicle) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setVehicle(vehicle);
        return driverRepository.save(driver);
    }

    public void save(Driver driver) {
        driverRepository.save(driver);
    }
}
