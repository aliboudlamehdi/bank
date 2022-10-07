package com.Bank.bank.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Bank.bank.entities.Account;
import com.Bank.bank.entities.Operation;
import com.Bank.bank.enums.OperationType;
import com.Bank.bank.exceptions.AccountNotFoundException;
import com.Bank.bank.exceptions.BalanceNotSufficientException;
import com.Bank.bank.repositories.AccountRepository;
import com.Bank.bank.repositories.OperationRepository;

import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor
public class AccountService {
	private AccountRepository accountRepository;
	private OperationRepository operationRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository, OperationRepository operationRepository) {
		this.accountRepository = accountRepository;
		this.operationRepository = operationRepository;
	}

	public Account saveAccount(Account account) {
		account.setCreatedAt(new Date());
		Account savedAccount = accountRepository.save(account);
		return savedAccount;
	}

	public Account getAccount(String numAccount) throws AccountNotFoundException {
		Account account = accountRepository.findByNumAccount(numAccount);
		return account;
	}

	public List<Account> AccountList() {
		List<Account> accounts = accountRepository.findAll();
		return accounts;
	}

	public Operation debit(String numAccount, double amount)
			throws AccountNotFoundException, BalanceNotSufficientException {
		Operation operation = new Operation();
		Account account = accountRepository.findByNumAccount(numAccount);

		if (account.getBalance() - amount < 0) {
			throw new BalanceNotSufficientException("Balance Not Sufficient");
		}
		operation.setType(OperationType.DEBIT);
		operation.setAmount(amount);
		operation.setOperationDate(new Date());
		operation.setAccountId(account.getId());
		operationRepository.save(operation);
		account.setBalance(account.getBalance() - amount);
		accountRepository.save(account);
		return operation;
	}

	public Operation credit(String numAccount, double amount) throws AccountNotFoundException {
		Account account = accountRepository.findByNumAccount(numAccount);
		Operation operation = new Operation();
		operation.setType(OperationType.CREDIT);
		operation.setAmount(amount);
		operation.setOperationDate(new Date());
		operation.setAccountId(account.getId());
		operationRepository.save(operation);
		account.setBalance(account.getBalance() + amount);
		accountRepository.save(account);
		return operation;
	}

	public Account operationsList(String numAccount, Date dateDebut, Date dateFin) {
		List<Operation> operations = accountRepository.findByNumAccount(numAccount).getOperations();
		Account account = accountRepository.findByNumAccount(numAccount);
		List<Operation> oper = operations.stream()
				.filter(op -> op.getOperationDate().after(dateDebut) && op.getOperationDate().before(dateFin))
				.collect(Collectors.toList());
		account.setOperations(oper);
		return account;
	}

}
