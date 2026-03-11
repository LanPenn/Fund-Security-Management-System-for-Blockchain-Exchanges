package com.example.graduationdsign;

import com.example.graduationdsign.dao.AccountMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GraduationDsignApplicationTests {
    @Autowired
  AccountMapper accountMapper;
    @Test
    void contextLoads() {
       accountMapper.updateBalance(1,-100);

    }

}
