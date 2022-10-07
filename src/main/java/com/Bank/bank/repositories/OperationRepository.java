package com.Bank.bank.repositories;

import com.Bank.bank.entities.Operation;
import com.Bank.bank.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation,Long> {
  List<Operation> findByAccountId(Long accountId);
  Page<Operation> findByAccountIdOrderByOperationDateDesc(Long accountId, Pageable pageable);
}
