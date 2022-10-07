package com.Bank.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;

import com.Bank.bank.entities.Account;
import com.Bank.bank.entities.Operation;
import com.Bank.bank.enums.OperationType;
import com.Bank.bank.repositories.AccountRepository;
import com.Bank.bank.repositories.OperationRepository;
import com.Bank.bank.services.AccountService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = BankApplication.class)

public class AcountServiceTest {

	private AccountService accountService;
	@MockBean
	private AccountRepository accountRepository;
	@MockBean
	private OperationRepository operationRepository;

	@BeforeEach
	void setUp() {
		accountService = new AccountService(accountRepository, operationRepository);
	}

	@Test
	@Rollback(false)
	public void test1_saveAccount_ShouldSaveAccount() throws Exception {
		Account account = new Account("4000", Math.random() * 90000, new Date(), "accounttosave",
				"accounttosave@accounttosave");
		given(accountRepository.save(account)).willReturn(account);
		assertThat(account).isEqualTo(accountService.saveAccount(account));
		Assertions.assertEquals(account, accountService.saveAccount(account));
	}

	@Test
	public void test2_getAccount_ShouldReturnAccount() throws Exception {
		Account account = new Account("4000", Math.random() * 90000, new Date(), "accounttosave",
				"accounttosave@accounttosave");
		given(accountRepository.findByNumAccount("4000")).willReturn(account);
		Account account2 = accountService.getAccount("4000");
		assertThat(account2.getNumAccount()).isEqualTo("4000");
	}

	@Test
	public void test3_debit_ShouldReturnOperationDebit() throws Exception {
		Account account = new Account("4000", 2000, new Date(), "accounttosave", "accounttosave@accounttosave");
		when(accountRepository.save(Mockito.any(Account.class))).thenReturn(new Account());
		Account ac1 = accountService.saveAccount(account);
		Operation operation = new Operation();
		operation = new Operation(new Date(), 1000, OperationType.DEBIT, account.getId());

		account.setBalance(account.getBalance() - operation.getAmount());
		given(operationRepository.save(operation)).willReturn(operation);
		Operation operation1 = operationRepository.save(operation);
		assertThat(operation1.getType()).isEqualTo(OperationType.DEBIT);
	}

	@Test
	public void test4_credit_ShouldReturnOperationCredit() throws Exception {
		Account account = new Account("4000", 2000, new Date(), "accounttosave", "accounttosave@accounttosave");
		given(accountRepository.save(account)).willReturn(account);
		Operation operation = new Operation();
		operation = new Operation(new Date(), 1000, OperationType.CREDIT, account.getId());
		account.setBalance(account.getBalance() + operation.getAmount());
		given(operationRepository.save(operation)).willReturn(operation);
		Operation operation1 = operationRepository.save(operation);
		assertThat(operation1.getType()).isEqualTo(OperationType.CREDIT);
	}

	@Test
	public void test5_ListOperation_ShouldReturnOperations() throws Exception {
		List<Operation> operations = new ArrayList();
		Account account = new Account("4000", 2000, new Date(), "accounttosave", "accounttosave@accounttosave");
		operations.add(new Operation(new Date("02/10/2022"), 1000, OperationType.CREDIT, account.getId()));
		operations.add(new Operation(new Date("02/10/2022"), 1000, OperationType.DEBIT, account.getId()));
		operations.add(new Operation(new Date("03/10/2022"), 1000, OperationType.DEBIT, account.getId()));
		account.setOperations(operations);
		given(accountRepository.save(account)).willReturn(account);
		Account account1 = accountRepository.save(account);
		assertThat(account1.getOperations().stream()
				.filter(op -> op.getOperationDate().after(new Date("01/10/2022"))
						&& op.getOperationDate().before(new Date("03/10/2022")))
				.collect(Collectors.toList()).size()).isEqualTo(2);
	}

}
