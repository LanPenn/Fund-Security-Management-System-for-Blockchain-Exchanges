package com.example.graduationdsign.service.impl;

import com.example.graduationdsign.dao.TradeMarketMapper;
import com.example.graduationdsign.dao.UserMapper;
import com.example.graduationdsign.entity.ResponseJson;
import org.springframework.beans.factory.annotation.Autowired;

public class aaa {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    ResponseJson responseJson;
    @Autowired
    TradeMarketMapper tradeMarketMapper;

    public ResponseJson updateStatus(int userId, int status) {
        boolean success = userMapper.updateStatus(userId,status)>0;
        return responseJson.getCode(success,"用户状态修改为"+status);
    }
    public ResponseJson ApproveOrders(int tradeId) {
        boolean success = tradeMarketMapper.updateStatus2(tradeId)>0;
        return success? responseJson.getCode(null,"订单更新成功"):responseJson.fail("订单更新失败，请稍后重试");
    }
}
