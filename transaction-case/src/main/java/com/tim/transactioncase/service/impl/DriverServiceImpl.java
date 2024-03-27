package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.repository.DriverRepository;
import com.tim.transactioncase.request.CreateDriverRequest;
import com.tim.transactioncase.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver createDriver(String name, String mobile) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setMobile(mobile);
        return driverRepository.save(driver);
    }

    public void save(Driver driver) {
        driverRepository.save(driver);
    }
    public void saveAll(List<Driver> driver) {
        driverRepository.saveAll(driver);
    }

    @Override
    public Driver findDriverById(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        return driver.orElse(null);
    }

    @Override
    public List<Driver> findDriversByIds(List<Long> driverIds) {
        return driverRepository.findAllById(driverIds);
    }

    @Override
    public Driver assignVehicleToDriver(Long driverId, Vehicle vehicle) {
        Driver driver = findDriverById(driverId);
        if (driver != null) {
            driver.setVehicle(vehicle);
            driverRepository.save(driver);
        }
        return driver;
    }

    @Override
    public List<Driver> createDrivers(List<CreateDriverRequest> requests) {
        List<Driver> drivers = new ArrayList<>();
        for (CreateDriverRequest request : requests) {
            Driver driver = new Driver();
            driver.setName(request.getName());
            driver.setMobile(request.getMobile());
            drivers.add(driver);
        }
        return driverRepository.saveAll(drivers);
    }

    @Override
    @Transactional
    public List<Driver> createDriversV2(List<CreateDriverRequest> requests) {
        List<Driver> drivers = new ArrayList<>();
        for (CreateDriverRequest request : requests) {
            Driver driver = new Driver();
            driver.setName(request.getName());
            driver.setMobile(request.getMobile());
            drivers.add(driver);
        }
        return driverRepository.saveAll(drivers);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Driver> getListDriver() {
        return driverRepository.findAll();
    }
}
