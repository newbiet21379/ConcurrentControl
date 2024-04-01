package com.tim.transactioncase.controller;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Vehicle;
import com.tim.transactioncase.request.CreateDriverRequest;
import com.tim.transactioncase.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/{version}/drivers")
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody CreateDriverRequest request, @PathVariable("version") String version) {
        Driver driver = driverService.createDriver(request.getName(), request.getMobile());
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<Driver> findDriverById(@PathVariable Long driverId, @PathVariable("version") String version) {
        Driver driver = driverService.findDriverById(driverId);
        if (driver != null) {
            return ResponseEntity.ok(driver);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{driverId}/vehicle")
    public ResponseEntity<Driver> assignVehicleToDriver(@PathVariable Long driverId, @RequestBody Vehicle vehicle, @PathVariable("version") String version) {
        Driver driver = driverService.assignVehicleToDriver(driverId, vehicle);
        if (driver != null) {
            return ResponseEntity.ok(driver);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Driver>> createDrivers(@Valid @RequestBody List<CreateDriverRequest> requests, @PathVariable("version") String version) {
        List<Driver> drivers;
        if(version.equals("v1")){
            drivers = driverService.createDrivers(requests);
        }else {
            drivers = driverService.createDriversV2(requests);
        }
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Driver>> getAllDrivers(@PathVariable String version) {
        if(version.equals("v2")){
            List<Driver> drivers = driverService.getListDriver();
            return ResponseEntity.ok(drivers);
        }
        return ResponseEntity.ok(Collections.emptyList());
    }
}