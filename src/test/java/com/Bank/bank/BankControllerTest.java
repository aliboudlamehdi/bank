package com.Bank.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.Bank.bank.dtos.CreditDTO;
import com.Bank.bank.dtos.DebitDTO;
import com.Bank.bank.entities.Account;
import com.Bank.bank.entities.Operation;
import com.Bank.bank.enums.OperationType;
import com.Bank.bank.repositories.AccountRepository;
import com.Bank.bank.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = {BankApplication.class},
properties = {"spring.cloud.config.enabled=false"})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class BankControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	AccountService accountService;
	@MockBean
	AccountRepository accountRepository;
	@Test
	@Rollback(false)
	public void addaccount_ShouldSaveAccount() throws Exception {
		Account account = new Account("4000", 8000, new Date(), "accounttosave", "accounttosave@accounttosave");
		given(accountService.saveAccount(account)).willReturn(account);
		mvc.perform(post("/accounts/addaccount").contentType(MediaType.APPLICATION_JSON).content(asJsonString(account))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		verify(accountService).saveAccount(account);
	}

	@Test
	public void getAccount_ShouldReturnAccount() throws Exception {
		Account account = new Account("4000", Math.random() * 90000, new Date(), "accounttosave",
				"accounttosave@accounttosave");
		accountService.saveAccount(account);
		given(accountService.getAccount("4000")).willReturn(account);
		mvc.perform(get("/accounts/4000")).andExpect(status().isOk()).andExpect(jsonPath("numAccount").value("4000"));
		assertThat(accountService.getAccount("4000").getNumAccount()).isEqualTo("4000");
	}

	@Test
	public void operationDebit_ShouldSaveDebit() throws Exception {
		Account account = new Account("4000", 4000, new Date(), "accounttosave", "accounttosave@accounttosave");
		Operation operation = new Operation(new Date(), 1000, OperationType.DEBIT, account.getId()); // when(accountService.accountSearch("1000").getId()).thenReturn(new
																										// AccountDTO().getId());
		account.setBalance(account.getBalance() + operation.getAmount());
		accountService.saveAccount(account);
		given(accountService.debit("4000", 2000d)).willReturn(operation);
		DebitDTO debit = new DebitDTO();
		debit.setNumAccount("4000");
		debit.setAmount(1000d);
		mvc.perform(post("/accounts/debit").contentType(MediaType.APPLICATION_JSON).content(asJsonString(debit))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		verify(accountService).debit("4000", 1000);
	}

	@Test
	public void operationCredit_ShouldSaveCredit() throws Exception {
		Account account = new Account("4000", 2000, new Date(), "accounttosave", "accounttosave@accounttosave");
		Operation operation = new Operation(new Date(), 1000d, OperationType.CREDIT, account.getId()); // when(accountService.accountSearch("1000").getId()).thenReturn(new
																										// AccountDTO().getId());
		account.setBalance(account.getBalance() + operation.getAmount());
		accountService.saveAccount(account);
		given(accountService.debit("4000", 1000d)).willReturn(operation);
		CreditDTO credit = new CreditDTO();
		credit.setNumAccount("4000");
		credit.setAmount(1000d);

		MvcResult result = mvc.perform(post("/accounts/credit").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(credit)).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andReturn();

		verify(accountService).credit("4000", 1000d);
	}

	@Test
	public void listOperations_ShouldReturnListOperations() throws Exception {
		List<Operation> operations= new ArrayList();
		Account account = new Account("4000", 2000, new Date(), "accounttosave", "accounttosave@accounttosave");
		accountService.saveAccount(account);
		account=accountService.getAccount("4000");
		Operation operation1=accountService.credit(account.getNumAccount(),1000);
		Operation operation2=accountService.debit(account.getNumAccount(),1000);
		Operation operation3=accountService.credit(account.getNumAccount(),1000);		
		operations.add(operation1);
		operations.add(operation2);		
		operations.add(operation3);
		account.setBalance(account.getBalance() + operation1.getAmount());
		account.setBalance(account.getBalance() - operation2.getAmount());
		account.setBalance(account.getBalance() + operation3.getAmount());
		account.setOperations(operations);		
		account=accountRepository.save(account);
		String dateDebut="02/10/2022";
		String dateFin="11/10/2022";		
		given(accountService.getAccount("4000")).willReturn(account);
		mvc.perform(get("/operations/4000").param("dateDebut", dateDebut)
	            .param("dateFin", dateFin)).andExpect(status().isOk()).andExpect(jsonPath("numAccount").value("4000"));
		assertThat(accountService.getAccount("4000").getOperations().stream()
				.filter(op -> op.getOperationDate().after(new Date("02/10/2022"))
						&& op.getOperationDate().before(new Date("11/10/2022")))
				.collect(Collectors.toList()).size()).isEqualTo(2);
	}

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			System.out.println(jsonContent);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
