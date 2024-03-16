package com.tim.concurrentcontrol.service;

import com.tim.concurrentcontrol.model.BankAccount;
import com.tim.concurrentcontrol.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankService bankService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenConcurrentAccess_thenCorrectTransaction() throws InterruptedException {
        BankAccount testAccount = new BankAccount();
        testAccount.setId(1L);
        testAccount.setBalance(5000.0);

        when(bankAccountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 100; i++) {
            service.submit(() -> bankService.deposit(testAccount.getId(), 10));
            service.submit(() -> bankService.withdraw(testAccount.getId(), 5));
        }

        service.shutdown();
        service.awaitTermination(60, TimeUnit.SECONDS);

        assertEquals(5500.0, bankService.getBalance(testAccount.getId()));
    }
}
