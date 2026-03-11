package com.example.graduationdsign.utils;

import com.example.graduationdsign.entity.TransactionRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionMonitor {
    private static final Map<Integer, List<TransactionRecord>> transactionMap = new ConcurrentHashMap<>();
    private static final Map<Integer, List<TransactionRecord>> transactionMap2 = new ConcurrentHashMap<>();
    private static final int MAX_TRANSACTIONS = 5; // 1分钟内最多允许的交易次数
    private static final long TIME_WINDOW = 1 * 60 * 1000; // 1分钟的时间窗口（毫秒）

    // 添加交易记录
    public static void addTransaction(int userId, String transactionType) {
        List<TransactionRecord> records = transactionMap.getOrDefault(userId, new ArrayList<>());
        List<TransactionRecord> records2 = transactionMap2.getOrDefault(userId, new ArrayList<>());
        if(transactionType.equals("cancel")){
            records2.add(new TransactionRecord(userId, new Date(), transactionType));
            transactionMap2.put(userId, records2);
        }else{
            records.add(new TransactionRecord(userId, new Date(), transactionType));
            transactionMap.put(userId, records);
        }

    }

    // 检查交易频率
    public static int isTransactionAllowed(int userId) {
        List<TransactionRecord> records = transactionMap.getOrDefault(userId, new ArrayList<>());
        List<TransactionRecord> records2 = transactionMap2.getOrDefault(userId, new ArrayList<>());
        long currentTime = System.currentTimeMillis();

        // 过滤出最近5分钟内的交易记录
        List<TransactionRecord> recentTransactions = new ArrayList<>();
        List<TransactionRecord> recentTransactions2 = new ArrayList<>();
        for (TransactionRecord record : records) {
            if (currentTime - record.getTransactionTime().getTime() <= TIME_WINDOW) {
                recentTransactions.add(record);
            }
        }
        for (TransactionRecord record2 : records2) {
            if (currentTime - record2.getTransactionTime().getTime() <= TIME_WINDOW) {
                recentTransactions2.add(record2);
            }
        }

        // 更新用户的交易记录，只保留最近5分钟内的记录
        transactionMap.put(userId, recentTransactions);
        transactionMap2.put(userId, recentTransactions2);
        // 检查交易次数是否超过限制
        if(recentTransactions.size() > MAX_TRANSACTIONS && recentTransactions2.size() > MAX_TRANSACTIONS){
            return 3;//都超频
        }
        if(recentTransactions2.size() > MAX_TRANSACTIONS && recentTransactions.size() <= MAX_TRANSACTIONS){
            return 2;//只有取消超频
        }
        if(recentTransactions.size() > MAX_TRANSACTIONS && recentTransactions2.size() <= MAX_TRANSACTIONS){
            return 1;//只有创建超频
        }
        return 0;
    }
}