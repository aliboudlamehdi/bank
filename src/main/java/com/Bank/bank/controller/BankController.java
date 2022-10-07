package com.Bank.bank.controller;

import com.Bank.bank.entities.Account;
import com.Bank.bank.entities.Operation;
import com.Bank.bank.exceptions.AccountNotFoundException;
import com.Bank.bank.services.AccountService;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.Bank.bank.dtos.CreditDTO;
import com.Bank.bank.dtos.DebitDTO;
import com.Bank.bank.exceptions.BalanceNotSufficientException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "BankController", description = "REST API for BankController information")
@RestController
public class BankController {
	 
	private AccountService accountService;
	    public BankController(AccountService accountService) {
	        this.accountService = accountService;
	    }

	    @PostMapping("/accounts/addaccount")
	    @ResponseStatus(HttpStatus.CREATED)
	    public Account addAccount(@RequestBody Account account) throws AccountNotFoundException {
	    	return accountService.saveAccount(account);
	    }
	
	    @GetMapping("/accounts/{numAccount}")
	    public ResponseEntity getAccount(@PathVariable String numAccount) throws AccountNotFoundException {
	        return ResponseEntity.ok(accountService.getAccount(numAccount));
	    }

	    @GetMapping("/operations/{numAccount}")
	    public ResponseEntity getHistory(@PathVariable String numAccount,@RequestParam String dateDebut,@RequestParam String dateFin) {
	    	Date dateD=new Date();
			try {
				dateD = new SimpleDateFormat("dd/MM/yyyy").parse(dateDebut);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	    	Date dateF=new Date();
			try {
				dateF = new SimpleDateFormat("dd/MM/yyyy").parse(dateFin);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Account ac=accountService.operationsList(numAccount,dateD,dateF);
	        return ResponseEntity.ok(ac);
	    }
	    @PostMapping("/accounts/debit")
	    @ResponseStatus(HttpStatus.CREATED)
	    public Operation debit(@RequestBody DebitDTO debitDTO) throws AccountNotFoundException, BalanceNotSufficientException {
	    	return accountService.debit(debitDTO.getNumAccount(),debitDTO.getAmount());
	        //return debitDTO;
	    }
	    @PostMapping("/accounts/credit")
	    @ResponseStatus(HttpStatus.CREATED)
	    public Operation credit(@RequestBody CreditDTO creditDTO) throws AccountNotFoundException {
	    	return accountService.credit(creditDTO.getNumAccount(),creditDTO.getAmount());
	    }

}
