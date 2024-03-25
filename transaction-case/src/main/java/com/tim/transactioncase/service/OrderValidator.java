package com.tim.transactioncase.service;

import com.tim.transactioncase.request.OrderRequest;

public interface OrderValidator {
    boolean isValid(OrderRequest orderRequest);
}
