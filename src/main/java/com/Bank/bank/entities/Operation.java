package com.Bank.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

import com.Bank.bank.enums.OperationType;

import java.util.Date;
@Entity
@Data
@NoArgsConstructor 
@AllArgsConstructor
public class Operation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private Long accountId;
    public Operation(Date operationDate, double amount, OperationType type,Long accountId) {
		super();
		this.operationDate = operationDate;
		this.amount = amount;
		this.type = type;
		this.accountId = accountId;
	}
}
