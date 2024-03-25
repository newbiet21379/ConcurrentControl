package com.tim.transactioncase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tim.transactioncase.common.JobStatus;
import com.tim.transactioncase.model.Driver;
import com.tim.transactioncase.model.Job;
import com.tim.transactioncase.service.JobFlowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(JobFlowController.class)
class JobFlowControllerTest {

    @Mock
    private JobFlowService jobFlowService;

    @InjectMocks
    private JobFlowController jobFlowController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobFlowController).build();
    }

    @Test
    void createJobFlowTest() throws Exception {
        //Arrange
        Job job = new Job();
        job.setId(1L);
        job.setStatus(JobStatus.COMPLETED);

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setName("Driver1");

        List<String> orderList = new ArrayList<>();
        orderList.add("Order1");
        orderList.add("Order2");
        String detailInfo = "Details Here";

        when(jobFlowService.createJobFlow(any(), any(), any())).thenReturn(job);

        //Act & Assert
        mockMvc.perform(post("/job/create")
                        .contentType("application/json")
                        .content(asJsonString(new com.tim.transactioncase.controller.CreateJobFlowRequest(orderList, driver, Collections.singletonList(detailInfo)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testJobFullLifecycle() throws Exception {
        Driver driver = new Driver();
        driver.setId(1L);

        Job job = new Job();
        job.setId(1L);

        String orderList = "{\"orderList\": [\"order 1\", \"order 2\"], \"driver\": {\"id\": 1}, \"detailInfos\": [\"detail 1\", \"detail 2\"]}";

        when(jobFlowService.createJobFlow(any(), any(), any())).thenReturn(job);
        when(jobFlowService.assignJobToDriver(anyLong(), any())).thenReturn(job);

        // Step 1: Create a new job
        mockMvc.perform(MockMvcRequestBuilders.post("/job/create")
                        .contentType("application/json")
                        .content(orderList))
                .andDo(print())
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