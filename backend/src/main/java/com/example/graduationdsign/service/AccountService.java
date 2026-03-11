package com.example.graduationdsign.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationdsign.entity.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Service
public interface AccountService  {
    ResponseEntity<Map<String, Object>> Recharge (int userid, int value);
    ResponseEntity<String> RechargeETH (int userid, int value);
    ResponseEntity<Map<String, Object>> Withdraw ( int userid,  int value);
    ResponseEntity<String> WithdrawETH ( int userid,  int value);
    Account checkBalance(int userid );

}
