package com.tim.transactioncase.request;

import com.tim.transactioncase.common.ShipmentStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShipmentDto implements Serializable {
    private String shipmentInfo;
    private ShipmentStatus status;
}
