package com.tim.concurrentcontrol.controller;

import com.tim.concurrentcontrol.model.Product;
import com.tim.concurrentcontrol.repository.ProductRepository;
import com.tim.concurrentcontrol.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ProductControllerConcurrencyTest.Initializer.class)
public class ProductControllerConcurrencyTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductService productService;


    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest").withDatabaseName("test")
                    .withUsername("username").withPassword("password");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.flyway.locations=classpath:db/migration"
            );
        }
    }

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