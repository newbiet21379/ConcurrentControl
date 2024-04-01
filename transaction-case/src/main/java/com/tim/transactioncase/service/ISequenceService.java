package com.tim.transactioncase.service;

import java.util.concurrent.CompletableFuture;

public interface ISequenceService {
    CompletableFuture<Long> getNextSequence(String sequenceName);
    CompletableFuture<Long> getNextSequence(String sequenceName, Long step);
}
