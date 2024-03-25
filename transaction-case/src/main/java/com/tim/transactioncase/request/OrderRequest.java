package com.tim.transactioncase.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderRequest implements Serializable {
    private String orderInfo;
    private List<String> detailInfos;

    public OrderRequest(String s, List<String> list) {
        this.orderInfo = s;
        this.detailInfos = list;
    }
}
