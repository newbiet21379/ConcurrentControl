package com.tim.transactioncase.service;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.request.OrderRequest;

import java.util.List;

public interface OrderService {

    Order createOrder(String orderInfo, List<String> details);

    Order save(Order order);
    List<Order> saveAll(List<Order> order);

    Order createOrderFlow(String orderInfo, List<String> detailInfos);

    void updateOrderFlow(Long orderId, String newOrderInfo);

    Order findOrderById(Long orderId);

    void createAndUpdateOrder(String orderName, List<String> detailInfos, String newName);

    void processOrderBatchWithValidation(List<OrderRequest> orders, OrderValidator orderValidator);

    List<Order> getOrdersByDriver(Driver driver);

    boolean isOrderCountMatchedWithRequest(Driver driver, Integer size);

    List<Order> findByDriverIds(List<Long> collect);
}