package com.example.graduationdsign.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationdsign.entity.Account;
import com.example.graduationdsign.dao.AccountMapper;
import com.example.graduationdsign.service.AccountService;
import com.example.graduationdsign.utils.TransactionMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>  implements AccountService {



    @Autowired
    AccountMapper AccountMapper;
    @Override
    public ResponseEntity<Map<String, Object>> Recharge(int userid, int value) {
        return getStringResponseEntity2(userid, value);
    }

    @Override
    public ResponseEntity<String> RechargeETH(int userid, int value) {
        if (value>0) {
            int success= AccountMapper.updateETH(userid,value);
            if (success==1) {
                System.out.println("充值eth:"+value);
                return ResponseEntity.ok("充值成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("充值失败");
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请输入大于0的数");
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>>  Withdraw(int userid, int value) {
        Account acc = AccountMapper.checkBalance(userid);
        int nowbau = acc.getBau();
        if(nowbau- value >=0 ){
            value=0-value;
            return getStringResponseEntity2(userid, value);
        }
        else{
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("data", " 提现金额超出账户余额");
            return  ResponseEntity.ok(response);
        }


    }

    @Override
    public ResponseEntity<String> WithdrawETH(int userid, int value) {
        Account acc = AccountMapper.checkBalance(userid);
        double nowbae = acc.getBae();
        if(nowbae- value >=0 ){
            System.out.println("提现金额:"+value);
            value=0-value;
            System.out.println("传入参数"+value);
                int success= AccountMapper.updateETH(userid,value);
                if (success==1) {
                    System.out.println("提现eth:"+value);
                    return ResponseEntity.ok("提现成功");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("提现失败");
                }

        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("提现金额超出账户余额");
        }
    }


    @Override
    public Account checkBalance(int userid) {
         return AccountMapper.checkBalance(userid);
    }




    private ResponseEntity<Map<String, Object>> getStringResponseEntity2(int userid, int value) {
        Map<String, Object> response = new HashMap<>();
            AccountMapper.updateStatus(userid,1);
        if (value > 0) {
            int success = AccountMapper.updateBalance(userid, value);
            if (success == 1) {
                System.out.println("充值$:" + value);
                response.put("status", 200);
                response.put("data", "充值成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", 400);
                response.put("data", "充值失败");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            int success = AccountMapper.updateBalance(userid, value);
            if (success == 1) {
                System.out.println("提现$:" + value);
                response.put("status", 200);
                response.put("data", "提现成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", 400);
                response.put("data", "提现失败");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
    }
}
