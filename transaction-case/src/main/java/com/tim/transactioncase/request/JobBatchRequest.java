package com.tim.transactioncase.request;

import lombok.Data;

import java.util.List;

@Data
public class JobBatchRequest {
    List<CreateJobFlowRequest> jobFlowRequests;
    boolean isAsync;
}
