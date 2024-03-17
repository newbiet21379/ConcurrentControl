package com.tim.concurrentcontrol.repository;

import com.tim.concurrentcontrol.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
