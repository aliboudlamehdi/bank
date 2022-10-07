package com.Bank.bank.repositories;

import java.util.Optional;

import com.Bank.bank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {

	Account findByNumAccount(String numAccount);
}
