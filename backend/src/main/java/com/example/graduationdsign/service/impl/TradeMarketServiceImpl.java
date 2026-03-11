package com.example.graduationdsign.service.impl;
import com.example.graduationdsign.dao.AccountMapper;
import com.example.graduationdsign.dao.TradeMarketMapper;
import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.entity.TradeOrder;
import com.example.graduationdsign.entity.tradeAlert;
import com.example.graduationdsign.service.TradeMarketService;
import com.example.graduationdsign.utils.TransactionMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalTime.now;

@Service
public class TradeMarketServiceImpl implements TradeMarketService {
    @Autowired
    ResponseJson responseJson;
    @Autowired
    TradeMarketMapper tradeMarketMapper;
    @Autowired
     AccountMapper   accountMapper;
    @Autowired
    com.example.graduationdsign.dao.alterMapper alterMapper;
    @Override
    public ResponseJson CreateOrder(TradeOrder tradeOrder) {
        int userid = Integer.parseInt(tradeOrder.getUserId());
        TransactionMonitor.addTransaction(userid, "Recharge");
        if (TransactionMonitor.isTransactionAllowed(userid)==1 || TransactionMonitor.isTransactionAllowed(userid)==3) {
            alterMapper.insert( new tradeAlert(Integer.parseInt(tradeOrder.getUserId()),"高频交易订单",
                    "用户在1分钟内创建了5个订单",LocalDateTime.now(),tradeOrder.getTradeId()));
            return responseJson.valueAbnormal("创建订单频率过高，请稍后再试");
        }

        Double amount = tradeOrder.getAmount();
        Double money = tradeOrder.getMoney();
        if(amount<0|| money<0){
            return responseJson.fail("金额必须大于0");
        }
        tradeOrder.setCreateTime(LocalDateTime.now());
        boolean success = tradeMarketMapper.insert(tradeOrder) > 0;
        Integer TradeId = tradeOrder.getTradeId();
        valueTest(tradeOrder);
        return success ? responseJson.getCode(tradeOrder,"创建订单成功"): responseJson.fail("订单创建失败，请稍后重试");
    }

    @Override
    public ResponseJson CancelOrder( int tradeId,int userId) {
        TransactionMonitor.addTransaction(userId, "cancel");
        if (TransactionMonitor.isTransactionAllowed(userId)==2 || TransactionMonitor.isTransactionAllowed(userId)==3) {
            alterMapper.insert( new tradeAlert(userId,"高频撤销订单",
                    "用户在1分钟内取消了5个订单",LocalDateTime.now(),tradeId));
            return responseJson.valueAbnormal("撤销订单频率过高，请稍后再试");
        }
        boolean success = tradeMarketMapper.deleteByUserId(tradeId) > 0;
        System.out.println(success);
        return success ? responseJson.getCode(null,"删除订单成功"): responseJson.fail("订单删除失败，请稍后重试");
    }

    @Override
    public ResponseJson GetOrders(String userId) {

        List<TradeOrder> tradeOrders = tradeMarketMapper.selectByUserId(userId);
        return responseJson.getCode(tradeOrders,"查询订单成功");
    }

    @Override
    public ResponseJson GetOrdersByType(String userId, String tradeType) {
        List<TradeOrder> tradeOrders = tradeMarketMapper.selectByTradeType(userId,tradeType);
        for(TradeOrder tradeorder:tradeOrders){
            tradeorder.setUserId(tradeMarketMapper.selectUsername(tradeorder.getUserId()));
        }


        return responseJson.getCode(tradeOrders,"查询订单成功");
    }

    @Override
    public ResponseJson UpdateOrder(int tradeId) {
        boolean success = tradeMarketMapper.updateStatus(tradeId)>0 ;
        return success? responseJson.getCode(null,"订单更新成功"):responseJson.fail("订单更新失败，请稍后重试");
    }

    @Override
    public ResponseJson getOrdersByStatus(int userId,int status) {
        List<TradeOrder> OverOrders= tradeMarketMapper.selectOverOrders(userId,status) ;
        return responseJson.getCode(OverOrders,"查询订单成功");
    }

    @Override
    public ResponseJson getExamineOrders(int userId) {
        List<TradeOrder> OverOrders= tradeMarketMapper.selectExamineOrders(userId) ;
        return responseJson.getCode(OverOrders,"查询待审批订单成功");
    }

    @Override
    public ResponseJson updateStatus2(int tradeId) {
        boolean success = tradeMarketMapper.updateStatus2(tradeId)>0;
        return success? responseJson.getCode(null,"订单更新成功"):responseJson.fail("订单更新失败，请稍后重试");
    }


    public boolean valueTest(TradeOrder tradeOrder) {
        if(tradeOrder.getStatus()==3){
            alterMapper.insert( new tradeAlert(Integer.parseInt(tradeOrder.getUserId()),"大额订单",
                    "用户一次性交易金额超过余额的50%",LocalDateTime.now(),tradeOrder.getTradeId()));
            return true;
        }

      return false;
    }
}
