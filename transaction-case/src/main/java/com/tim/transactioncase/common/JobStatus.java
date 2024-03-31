package com.tim.transactioncase.common;

import lombok.Getter;

@Getter
public enum JobStatus {
    CREATED,
    PENDING,
    PLANNED,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}
