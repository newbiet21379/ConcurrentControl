package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.repository.DriverRepository;
import com.tim.transactioncase.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

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
