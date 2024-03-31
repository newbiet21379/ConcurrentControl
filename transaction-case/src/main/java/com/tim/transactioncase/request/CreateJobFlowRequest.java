package com.tim.transactioncase.request;

import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.common.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CreateJobFlowRequest implements Serializable {
    private Long driverId;
    private String orderInfo;
    private String detailInfo;
    private String shipmentInfo;
    private String presetLine;
    private JobStatus jobStatus;
    private ShipmentStatus shipmentStatus;
}
