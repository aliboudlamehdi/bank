package com.Bank.bank.entities;

import lombok.*;

import javax.persistence.*;

import com.Bank.bank.enums.AccountStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class Account {
    public Account(String numAccount, double balance, Date createdAt, String customerName, String customerEmail) {
		super();
		this.numAccount = numAccount;
		this.balance = balance;
		this.createdAt = createdAt;
		CustomerName = customerName;
		CustomerEmail = customerEmail;
		
	}
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    private String numAccount;    
    private double balance;
    private Date createdAt;
    private String CustomerName;
    private String CustomerEmail;
    @OneToMany
    //(mappedBy = "account",fetch = FetchType.LAZY)
    @JoinColumn(name="accountId", referencedColumnName = "id")
    private List<Operation> operations= new ArrayList();
    
}
