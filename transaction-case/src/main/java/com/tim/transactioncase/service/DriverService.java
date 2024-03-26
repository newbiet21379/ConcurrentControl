package com.tim.transactioncase.service;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Vehicle;

public interface DriverService {
    public Driver createDriver(String name, Vehicle vehicle);
    public void save(Driver driver);
}
