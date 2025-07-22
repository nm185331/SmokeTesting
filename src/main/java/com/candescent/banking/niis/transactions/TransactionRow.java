package com.candescent.banking.niis.transactions;

public class TransactionRow {
    public String date;
    public String description;
    public String amount;
    public String balance;

    public TransactionRow(String date, String description, String amount, String balance) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.balance = balance;
    }
}
