package com.tim.concurrentcontrol.service;

import com.tim.concurrentcontrol.common.BankException;
import com.tim.concurrentcontrol.model.BankAccount;
import com.tim.concurrentcontrol.repository.BankAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BankService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Transactional(readOnly = true)
    public BankAccount getAccount(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankException.AccountNotFoundException(id));
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void withdraw(Long accountId, double amount) {
        BankAccount account = getAccount(accountId);
        if (account.getBalance() < amount) {
            throw new BankException.InsufficientFundsException(accountId, amount);
        }

        account.setBalance(account.getBalance() - amount);
        bankAccountRepository.save(account);
    }

    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    public void deposit(Long accountId, double amount) {
        BankAccount account = getAccount(accountId);
        account.setBalance(account.getBalance() + amount);
        bankAccountRepository.save(account);
    }

    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    public double getBalance(Long accountId) {
        Optional<BankAccount> account = bankAccountRepository.findById(accountId);
        if(account.isPresent())
            return account.get().getBalance();
        throw new BankException.AccountNotFoundException(accountId);
    }
}