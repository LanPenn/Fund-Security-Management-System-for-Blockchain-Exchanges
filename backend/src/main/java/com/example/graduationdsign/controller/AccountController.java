package com.example.graduationdsign.controller;

import com.example.graduationdsign.dao.alterMapper;
import com.example.graduationdsign.entity.Account;
import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.entity.tradeAlert;
import com.example.graduationdsign.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
@Autowired
com.example.graduationdsign.dao.alterMapper alterMapper;
 @Autowired
 ResponseJson responseJson;

    @PostMapping("/recharge")
    public ResponseEntity<Map<String, Object>> recharge(@RequestParam int userId, @RequestParam int value) {
        return accountService.Recharge(userId, value);

    }

    @PostMapping("/Withdraw")
    public ResponseEntity<Map<String, Object>> Withdraw(@RequestParam int userId, @RequestParam int value) {
        return accountService.Withdraw(userId, value);

    }

    @PostMapping("/rechargeETH")
    public ResponseEntity<String> rechargeETH(@RequestParam int userId, @RequestParam int value) {
        return accountService.RechargeETH(userId, value);

    }

    @PostMapping("/WithdrawETH")
    public ResponseEntity<String> WithdrawETH(@RequestParam int userId, @RequestParam int value) {
        return accountService.WithdrawETH(userId, value);

    }

    @PostMapping("/balance")
    public Account checkBalance(@RequestParam int userId) {
     return  accountService.checkBalance(userId );
    }



}
