package com.tim.concurrentcontrol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tim.concurrentcontrol.model.User;
import com.tim.concurrentcontrol.service.UserService;
import com.tim.concurrentcontrol.util.SharedPostgreSQLContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { SharedPostgreSQLContainer.class })
class UserControllerConcurrencyTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = SharedPostgreSQLContainer.getInstance();

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
                                        put("/api/users/" + userId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(new ObjectMapper().writeValueAsString(user)))
                                .andReturn();

                        User userTest = userService.getUser(userId);

                        assertThat(user.getName()).isEqualTo(userTest.getName()); // 200 OK status expected

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}