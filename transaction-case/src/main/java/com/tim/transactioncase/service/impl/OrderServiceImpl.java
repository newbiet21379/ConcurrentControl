package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.OrderExecute;
import com.tim.transactioncase.repository.OrderRepository;
import com.tim.transactioncase.request.OrderRequest;
import com.tim.transactioncase.service.OrderService;
import com.tim.transactioncase.service.OrderValidator;
import org.springframework.stereotype.Service;
import com.tim.transactioncase.model.Order;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

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

    public Order save(Order order) {
        return orderRepository.save(order);
    }
    public List<Order> saveAll(List<Order> order) {
        return orderRepository.saveAll(order);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrderFlow(String orderInfo, List<String> detailInfos) {
        return generateOrder(orderInfo, detailInfos);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateOrderFlow(Long orderId, String newOrderInfo) {
        updateOrder(orderId, newOrderInfo);
    }

    private Order generateOrder(String orderInfo, List<String> detailInfos) {
        Order order = this.createOrder(orderInfo, detailInfos);
        return this.save(order);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateOrder(Long orderId, String newOrderInfo) {
        Order order = this.findOrderById(orderId);
        order.setOrderInfo(newOrderInfo);
        this.save(order);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order findOrderById(Long orderId) {
        Optional<Order> order = this.orderRepository.findById(orderId);

        return order.orElse(null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createAndUpdateOrder(String orderName, List<String> detailInfos, String newName) {
        Order order = this.createOrder(orderName, detailInfos);
        this.save(order);

        // simulate an error
        if (true) {
            throw new RuntimeException("Error occurred");
        }

        order.setOrderInfo(newName);
        this.save(order);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderBatchWithValidation(List<OrderRequest> orders, OrderValidator orderValidator) {
        List<Order> newOrders = new ArrayList<>();
        for (OrderRequest orderRequest : orders) {
            if (!orderValidator.isValid(orderRequest)) {
                throw new IllegalArgumentException("Invalid order in batch: " + orderRequest);
            }
            Order order = createOrderFlow(orderRequest.getOrderInfo(), orderRequest.getDetailInfos());
            newOrders.add(order);
        }

        for (Order order : newOrders) {
            save(order);
        }
    }

    public List<Order> getOrdersByDriver(Driver driver) {
        return orderRepository.getOrdersByDriver(driver);
    }

    public boolean isOrderCountMatchedWithRequest(Driver driver, Integer count) {
        return orderRepository.isOrderCountMatchedWithRequest(driver, count);
    }

    public List<Order> findByDriverIds(List<Long> driverIds) {
        return orderRepository.findByDriverIds(driverIds);
    }

}
