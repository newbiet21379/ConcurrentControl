package com.tim.transactioncase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.*;
import com.tim.transactioncase.request.CreateJobFlowRequest;
import com.tim.transactioncase.service.JobFlowService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class JobFlowControllerTest {

    @Mock
    private JobFlowService jobFlowServiceImpl;
    @InjectMocks
    private JobFlowController jobFlowController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobFlowController).build();
    }

    @Test
    void createJobFlowTest() throws Exception {
        Job result = generateJobTest();

        String detailInfo = "detailInfo";

        when(jobFlowServiceImpl.createJobFlow(any(), any(), any())).thenReturn(result);

        //Act & Assert
        mockMvc.perform(post("/job/create")
                        .contentType("application/json")
                        .content(asJsonString(new CreateJobFlowRequest(result.getOrders(), result.getDriver(), Collections.singletonList(detailInfo)))))
                .andExpect(status().isOk());
    }

    @NotNull
    private static Job generateJobTest() {
        Job result = new Job();
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        // Set other properties of shipment here
        List<Order> orderList = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderInfo("Order1");
        order1.setShipment(shipment);
        orderList.add(order1);

        Order order2 = new Order();
        order2.setOrderInfo("Order2");
        order2.setShipment(shipment);
        orderList.add(order2);

        //Arrange
        Job job = new Job();
        job.setId(1L);
        job.setStatus(JobStatus.COMPLETED);

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setName("Driver1");
        return result;
    }

    @Test
    void testJobFullLifecycle() throws Exception {
        Driver driver = new Driver();
        driver.setId(1L);

        Job job = generateJobTest();
        job.setId(1L);

        String detailInfo = "detailInfo";
        when(jobFlowServiceImpl.createJobFlow(any(), any(), any())).thenReturn(job);
        when(jobFlowServiceImpl.assignJobToDriver(anyLong(), any())).thenReturn(job);

        // Step 1: Create a new job
        mockMvc.perform(MockMvcRequestBuilders.post("/job/create")
                        .contentType("application/json")
                        .content(asJsonString(new CreateJobFlowRequest(job.getOrders(), job.getDriver(), Collections.singletonList(detailInfo)))))
                .andExpect(status().isOk());

        // Step 2: Assign job to driver
        mockMvc.perform(MockMvcRequestBuilders.post("/job/assign/{jobId}", job.getId())
                        .contentType("application/json")
                        .content("{\"id\": 1}"))
                .andDo(print())
                .andExpect(status().isOk());

        // Step 3: Walk through each status and update
        Arrays.stream(JobStatus.values()).forEach(status -> {
            try {
                mockMvc.perform(MockMvcRequestBuilders.put("/job/update/{id}/{status}", job.getId(), status)
                                .contentType("application/json"))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}