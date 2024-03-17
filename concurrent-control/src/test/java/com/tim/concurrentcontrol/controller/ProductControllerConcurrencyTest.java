package com.tim.concurrentcontrol.controller;

import com.tim.concurrentcontrol.model.Product;
import com.tim.concurrentcontrol.service.ProductService;
import com.tim.concurrentcontrol.util.SharedPostgreSQLContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { SharedPostgreSQLContainer.class })
public class ProductControllerConcurrencyTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductService productService;


    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = SharedPostgreSQLContainer.getInstance();

    private static final int THREADS_NUM = 10;

    @Test
    public void testConcurrentPurchasesSuccess() {
        long productId = 1L;
        Product product= productService.findById(productId);

        IntStream.range(0, THREADS_NUM)
                .parallel()
                .forEach(i -> {
                    try {
                        mvc.perform(post("/products/" + productId + "/purchase"))
                                .andExpect(status().isOk());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        Product productTest= productService.findById(productId);
        Assertions.assertEquals(product.getQuantity() - 10, productTest.getQuantity());

    }

    @Test
    public void testConcurrentPurchasesFailure() {
        Long productId = 2L;

        productService.updateProductStock(productId, 5);

        int threads = 10;
        IntStream.range(0, threads)
                .parallel()
                .forEach(i -> {
                    try {
                        MvcResult result = mvc.perform(post("/products/" + productId + "/purchase")).andReturn();
                        int status = result.getResponse().getStatus();
                        if (productService.findById(productId).getQuantity() > 0) {
                            assertThat(status).isEqualTo(200);
                        } else {
                            assertThat(status).isNotEqualTo(200);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}