package com.Bank.bank;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.Bank.bank.entities.Account;
import com.Bank.bank.entities.Operation;
import com.Bank.bank.enums.OperationType;
import com.Bank.bank.repositories.AccountRepository;
import com.Bank.bank.repositories.OperationRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(Alphanumeric.class)
public class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private OperationRepository operationRepository;

	@Test
	@Rollback(false)
	public void test1_saveAccount_ReturnAccount() {
		Account accountToSave = new Account("4000", Math.random() * 90000, new Date(), "accounttosave",
				"accounttosave@accounttosave");
		accountRepository.save(accountToSave);
		Account account = accountRepository.findByNumAccount("4000");
		assertThat(account.getCustomerName()).isEqualTo("accounttosave");
	}

	@Test
	public void test2_getAccount_ReturnAccount() {
		Account account = accountRepository.findById((long) 1).orElse(null);
		assertThat(account.getId()).isEqualTo(1L);
	}

	@Test
	public void test3_accountSearch_ReturnAccount() {
		Account account = accountRepository.findByNumAccount("4000");
		assertThat(account.getNumAccount()).isEqualTo("4000");
	}

	@Test
	@Rollback(false)
	public void test4_saveOperation_ReturnOperation() {
		List<Operation> operations = new ArrayList();
		Account account = accountRepository.findByNumAccount("4000");
		Operation operationToSave1 = new Operation(new Date(), 9999.00, OperationType.DEBIT, account.getId());
		account.setBalance(account.getBalance() - operationToSave1.getAmount());
		Operation operationToTest1 = operationRepository.save(operationToSave1);
		operations.add(operationToTest1);
		account.setOperations(operations);
		accountRepository.save(account);
		assertThat(operationToTest1.getAmount()).isEqualTo(9999.00);

	}

	@Test
	@Rollback(false)
	public void test5_saveOperation_ReturnListOperation() {
		Account accountToSave = new Account("5000", 10000, new Date(), "accounttosave", "accounttosave@accounttosave");
		accountRepository.save(accountToSave);
		Account account = accountRepository.findByNumAccount("5000");
		Operation operation1 = new Operation(new Date("01/10/2022"), 4000, OperationType.DEBIT, account.getId());
		account.setBalance(account.getBalance() - operation1.getAmount());
		operationRepository.save(operation1);
		Operation operation2 = new Operation(new Date("02/10/2022"), 5000, OperationType.CREDIT, account.getId());
		account.setBalance(account.getBalance() + operation2.getAmount());
		operationRepository.save(operation2);
		Operation operation3 = new Operation(new Date("03/10/2022"), 6000, OperationType.CREDIT, account.getId());
		account.setBalance(account.getBalance() + operation3.getAmount());
		operationRepository.save(operation3);
		List<Operation> operations = new ArrayList();
		operations.add(operation1);
		operations.add(operation2);
		operations.add(operation3);
		account.setOperations(operations);
		accountRepository.save(account);
		Account account1 = accountRepository.findByNumAccount("5000");
		assertThat(account1.getOperations().stream()
				.filter(op -> op.getOperationDate().after(new Date("01/10/2022"))
						&& op.getOperationDate().before(new Date("04/10/2022")))
				.collect(Collectors.toList()).size()).isEqualTo(2);
		assertThat(account1.getBalance()).isEqualTo(17000);

	}

}