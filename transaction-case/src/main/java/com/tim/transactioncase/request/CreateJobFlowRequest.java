package com.tim.transactioncase.request;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateJobFlowRequest implements Serializable {

    private List<Order> orderList;
    private Driver driver;
    private List<String> detailInfos;
}
