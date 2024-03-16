package com.tim.concurrentcontrol.common;

public class BankException {
    public static class LowBalanceException extends RuntimeException {
        public LowBalanceException(Long id) {
            super("Insufficient funds in account with id:" + id);
        }
    }

    // AccountNotFoundException.java
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(Long id) {
            super("Could not find account with id: " + id);
        }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(Long id, double amount) {
            super(String.format("Bank account with id: %s withdraw with amount %s is insufficient", id, amount));
        }
    }
}
