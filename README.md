# transaction-case

## Introduction

`transaction-case` is a powerful Java application designed with Spring Boot. The application showcases the use of concurrent control and transaction operations on several entities, such as Driver, Vehicle, Jobs, and others.

## Goals

This project aims to demonstrate:

- How concurrent control can be implemented in a Java application.
- The use of transactions for ensuring data integrity and consistency.

## Installation

We are utilizing Docker to simplify the application setup and environment management process. Use the following command to get started:

```shell
docker-compose -f docker-compose.yaml up -d
```
## Application Structure

Below are the key classes in the project:

**Services:**

- DriverService
- VehicleService
- JobService
- OrderService
- ShipmentService
- JobFlowService

**Repositories:**

- DriverRepository
- VehicleRepository

These classes are central to our demonstration of concurrent control and transaction operations in a real-world scenario.

## Configurations

To tweak application-specific configurations, modify the `application.properties` file in the `src/main/resources` directory.

## Learning Outcomes

Go through the source code and observe how each service class uses Spring's @Transactional annotation to ensure that operations either fully complete or fully rollback in case of any discrepancies.

Pay close attention to methods dealing with multiple operations on the database - these showcase how you can ensure consistency and integrity of your data when dealing with concurrency.



# transaction-case

## Introduction

`transaction-case` is a robust Java application built with Spring Boot. The application provides services related to operations on various entities like Driver, Vehicle, Job and so on.

## Installation

The project uses Docker for simplifying the process of setting up and managing the application's environment. Here's how you can get started:
```shell
docker-compose -f compose.yaml up -d
```

## Application Structure

Below are the core components of the project:

**Services:**

- DriverService
- VehicleService
- JobService
- OrderService
- ShipmentService
- JobFlowService

**Repositories:**

- DriverRepository
- VehicleRepository

These components provide a breadth of functionality for various operations related to their corresponding entities.

## Configuration

Modify the `application.properties` file in the `src/main/resources` directory for application-specific settings.