package com.Bank.bank.dtos;

import lombok.Data;

@Data
public class DebitDTO {
    private String numAccount;
    private double amount;
}
