package com.tim.concurrentcontrol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tim.concurrentcontrol.model.User;
import com.tim.concurrentcontrol.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = UserControllerConcurrencyTest.Initializer.class)

class UserControllerConcurrencyTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;
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

    @Test
    public void testConcurrentUpdates() throws Exception {
        Long userId = 1L;

        // Initialize user.
        User user = new User();
        user.setId(userId);
        user.setName("test user");
        userService.updateUser(userId, user.getName());

        int threads = 10;
        IntStream.range(0, threads)
                .parallel()
                .forEach(i -> {
                    try {
                        user.setName("test user " + i); // Each thread tries to update the user with a different name

                        MvcResult result = mvc.perform(
                                        put("/users/" + userId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(new ObjectMapper().writeValueAsString(user)))
                                .andReturn();

                        int status = result.getResponse().getStatus();
                        assertThat(status).isEqualTo(200); // 200 OK status expected

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}