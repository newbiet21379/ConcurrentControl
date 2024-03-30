package com.tim.transactioncase.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T doInNewTransaction(TransactionalCallable<T> callable) {
        return callable.execute();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public <T> T doInSameTransaction(TransactionalCallable<T> callable) {
        return callable.execute();
    }

    @Transactional(propagation = Propagation.NESTED)
    public <T> T doInNestedTransaction(TransactionalCallable<T> callable) {
        return callable.execute();
    }

    public interface TransactionalCallable<V> {
        V execute();
    }
}
