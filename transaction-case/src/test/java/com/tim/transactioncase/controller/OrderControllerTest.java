package com.tim.transactioncase.controller;

import com.tim.transactioncase.model.Order;
import com.tim.transactioncase.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController
        .class)
@SpringBootTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderServiceImpl orderServiceImpl;

    @Test
    void createOrderFlowTest() throws Exception {
        when(orderServiceImpl.createOrderFlow(anyString(), anyList())).thenReturn(new Order());

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .param("orderInfo", "New Order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Detail1\", \"Detail2\"]"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(orderServiceImpl, times(1)).createOrderFlow(anyString(), anyList());
    }

    @Test
    void updateOrderFlowTest() throws Exception {
        doNothing().when(orderServiceImpl).updateOrderFlow(anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.put("/orders/1")
                        .param("newOrderInfo", "Updated Order"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(orderServiceImpl, times(1)).updateOrderFlow(anyLong(), anyString());
    }
}