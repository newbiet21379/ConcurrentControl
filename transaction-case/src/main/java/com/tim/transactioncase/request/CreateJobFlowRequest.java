
package com.tim.transactioncase.controller;

import com.tim.transactioncase.model.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateJobFlowRequest implements Serializable {

    private List<String> orderList;
    private Driver driver;
    private List<String> detailInfos;
}
