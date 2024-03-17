package com.tim.transactioncase.service;

import com.tim.transactioncase.model.OrderExecute;
import com.tim.transactioncase.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tim.transactioncase.model.Order;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(String orderInfo, List<String> details) {
        Order order = new Order();
        order.setOrderInfo(orderInfo);

        List<OrderExecute> orderDetails = details.stream().map(detail -> {
            OrderExecute orderDetail = new OrderExecute();
            orderDetail.setDetailInfo(detail);
            orderDetail.setOrder(order);
            return orderDetail;
        }).collect(Collectors.toList());

        order.setOrderDetails(orderDetails);

        return orderRepository.save(order);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
