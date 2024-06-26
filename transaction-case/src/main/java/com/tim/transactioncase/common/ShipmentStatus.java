package com.tim.transactioncase.common;

import lombok.Getter;

@Getter
public enum ShipmentStatus {
    CREATED,
    PENDING,
    IN_TRANSIT,
    DELIVERED,
    FAILED
}