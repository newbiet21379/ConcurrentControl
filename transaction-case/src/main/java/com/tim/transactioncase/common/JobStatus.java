package com.tim.transactioncase.common;

import lombok.Getter;

@Getter
public enum JobStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}
