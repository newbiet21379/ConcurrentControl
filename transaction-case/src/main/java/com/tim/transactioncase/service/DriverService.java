package com.tim.transactioncase.service;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.request.CreateDriverRequest;

import java.util.List;

public interface DriverService {
    public Driver createDriver(String name, String mobile);
    public void save(Driver driver);

    Driver findDriverById(Long driverId);

    Driver assignVehicleToDriver(Long driverId, Vehicle vehicle);

    List<Driver> createDrivers(List<CreateDriverRequest> requests);

    List<Driver> createDriversV2(List<CreateDriverRequest> requests);

    List<Driver> getListDriver();
}
