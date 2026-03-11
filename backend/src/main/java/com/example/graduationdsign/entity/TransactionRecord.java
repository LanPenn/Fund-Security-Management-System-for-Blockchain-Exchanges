package com.example.graduationdsign.entity;

import java.util.Date;

public class TransactionRecord {
    private int userId;
    private Date transactionTime;
    private String transactionType; // "Recharge" 或 "Withdraw"

    public TransactionRecord(int userId, Date transactionTime, String transactionType) {
        this.userId = userId;
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
    }

    public int getUserId() {
        return userId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public String getTransactionType() {
        return transactionType;
    }
}