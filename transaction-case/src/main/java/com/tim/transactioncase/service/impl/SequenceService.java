package com.tim.transactioncase.service.impl;

import com.tim.transactioncase.service.ISequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SequenceService implements ISequenceService {

    private final ValueOperations<String, String> valueOperations;

    @Autowired
    public SequenceService(StringRedisTemplate template) {
        this.valueOperations = template.opsForValue();
    }

    @Async
    public CompletableFuture<Long> getNextSequence(String sequenceName) {
        // Redis increment operation is atomic
        Long nextValue = valueOperations.increment(sequenceName, 1);
        // Return the new value wrapped in a Future
        return CompletableFuture.completedFuture(nextValue);
    }

    @Async
    public CompletableFuture<Long> getNextSequence(String sequenceName, Long step) {
        // Redis increment operation is atomic
        Long nextValue = valueOperations.increment(sequenceName, step);
        // Return the new value wrapped in a Future
        return CompletableFuture.completedFuture(nextValue);
    }
}
