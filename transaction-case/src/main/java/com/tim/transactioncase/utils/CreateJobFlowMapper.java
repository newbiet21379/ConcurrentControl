package com.tim.transactioncase.utils;

import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.model.OrderExecute;
import com.tim.transactioncase.request.CreateJobFlowRequest;

import java.util.List;
import java.util.stream.Collectors;

public class CreateJobFlowMapper {

    public static List<Order> toOrderList(List<CreateJobFlowRequest> createJobRequests) {
        return createJobRequests.stream()
                .map(CreateJobFlowMapper::toOrder)
                .collect(Collectors.toList());
    }
    public static Order toOrder(CreateJobFlowRequest createJobRequest) {
        Order order = new Order();
        order.setOrderInfo(createJobRequest.getOrderInfo());

        OrderExecute orderExecute = new OrderExecute();
        orderExecute.setDetailInfo(createJobRequest.getDetailInfo());
        orderExecute.setOrder(order);

        order.setOrderDetails(List.of(orderExecute));

        return order;
    }

    public static List<Long> toDriverIdList(List<CreateJobFlowRequest> createJobRequests) {
        return createJobRequests.stream()
                .map(CreateJobFlowRequest::getDriverId)
                .collect(Collectors.toList());
    }

    public static List<String> toDetailInfoList(List<CreateJobFlowRequest> createJobRequests) {
        return createJobRequests.stream()
                .map(CreateJobFlowRequest::getDetailInfo)
                .collect(Collectors.toList());
    }
}