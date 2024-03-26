package com.tim.transactioncase.controller;

import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderServiceImpl;

    @Autowired
    public OrderController(OrderService orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @PostMapping
    public ResponseEntity<Order> createOrderFlow(@RequestParam String orderInfo, @RequestBody List<String> detailInfos) {
        Order order = orderServiceImpl.createOrderFlow(orderInfo, detailInfos);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderFlow(@PathVariable Long orderId, @RequestParam String newOrderInfo) {
        orderServiceImpl.updateOrderFlow(orderId, newOrderInfo);
        return ResponseEntity.ok().build();
    }
}
