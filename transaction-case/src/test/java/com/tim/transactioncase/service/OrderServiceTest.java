package com.tim.transactioncase.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.repository.OrderRepository;
import com.tim.transactioncase.request.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setOrderInfo("Old Order");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void createOrderFlowTest() {
        List<String> details = new ArrayList<>();
        details.add("Detail1");
        details.add("Detail2");

        Order result = orderService.createOrderFlow("New Order", details);

        // Verify save method was called
        verify(orderRepository, times(1)).save(any(Order.class));

        // Check returned order details
        assertEquals("New Order", result.getOrderInfo());
        assertEquals(2, result.getOrderDetails().size());
        assertTrue(result.getOrderDetails().stream().anyMatch(detail -> detail.getDetailInfo().equals("Detail1")));
        assertTrue(result.getOrderDetails().stream().anyMatch(detail -> detail.getDetailInfo().equals("Detail2")));
    }

    @Test
    void updateOrderFlowTest() {
        orderService.updateOrderFlow(1L, "Updated Order");

        // Verify save method was called
        verify(orderRepository, times(1)).save(any(Order.class));

        assertEquals("Updated Order", order.getOrderInfo());
    }

    @Test
    void createAndUpdateOrderTransactionTest() {
        List<String> details = new ArrayList<>();
        details.add("Detail1");
        details.add("Detail2");

        assertThrows(RuntimeException.class, () -> {
            orderService.createAndUpdateOrder("New Order", details, "Newer Order");
        });

        verify(orderRepository, times(1)).save(any(Order.class));
        assertNotNull(orderService.findOrderById(order.getId()));
        assertNotEquals("Newer Order", order.getOrderInfo());
    }

    @Test
    void processOrderBatchWithValidationTest() {
        List<OrderRequest> orderRequests = new ArrayList<>();
        orderRequests.add(new OrderRequest("Order 1", Arrays.asList("Detail 1.1", "Detail 1.2")));
        orderRequests.add(new OrderRequest("Order 2", Arrays.asList("Detail 2.1", "Detail 2.2")));

        OrderValidator validator = orderRequest -> {
            // Assume that our validation logic is
            // that the order info should not be 'Order 1'
            return !"Order 1".equals(orderRequest.getOrderInfo());
        };

        assertThrows(IllegalArgumentException.class, () ->
                orderService.processOrderBatchWithValidation(orderRequests, validator));

        verify(orderService, times(0)).save(any(Order.class));
        // This verifies that no orders were saved, since one of them was not valid.
    }
}
