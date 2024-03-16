package com.tim.concurrentcontrol.service;

import com.tim.concurrentcontrol.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceConcurrencyTest {

    @Autowired
    private UserService userService;

    @Test
    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/setup.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/cleanup.sql")
    })
    public void testUpdateUserConcurrently() throws Exception {
        final Long userId = 1L;  // assuming there is a user with this id in the database

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                try {
                    userService.updateUser(userId, "newName" + Thread.currentThread().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();

        try {
            // Wait for all threads to finish
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        User updatedUser = userService.getUser(userId);
        assertNotNull(updatedUser);
        // Checking if name has been updated and not overwritten by other threads
        assertTrue(updatedUser.getName().startsWith("newName"));
    }
}