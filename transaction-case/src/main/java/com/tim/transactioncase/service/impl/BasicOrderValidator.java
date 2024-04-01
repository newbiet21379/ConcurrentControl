package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.request.OrderRequest;
import com.tim.transactioncase.service.OrderValidator;
import org.springframework.stereotype.Component;

@Component
public class BasicOrderValidator implements OrderValidator {

    @Override
    public boolean isValid(OrderRequest orderRequest) {
        return orderRequest.getDetailInfos() != null && !orderRequest.getDetailInfos().isEmpty();
    }
}
