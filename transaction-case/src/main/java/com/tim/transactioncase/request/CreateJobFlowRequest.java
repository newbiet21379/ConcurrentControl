package com.tim.transactioncase.request;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import com.tim.transactioncase.model.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CreateJobFlowRequest implements Serializable {
    private Long driverId;
    private Long orderId;
    private String orderInfo;
    private String detailInfo;
    private String shipmentInfo;
    private JobStatus jobStatus;
    private ShipmentStatus shipmentStatus;
}
