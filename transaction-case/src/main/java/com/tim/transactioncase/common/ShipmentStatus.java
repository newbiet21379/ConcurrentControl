package com.tim.transactioncase.common;

import lombok.Getter;

@Getter
public enum ShipmentStatus {
    PENDING,
    IN_TRANSIT,
    DELIVERED,
    FAILED
}