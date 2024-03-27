package com.tim.transactioncase.common;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TransactionWrapper {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> void doInTransaction(TransactionalCallable<T> callable) {
        callable.execute();
    }

    public interface TransactionalCallable<V> {
        void execute();
    }
}
