package com.example.graduationdsign.service;

import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.entity.TradeOrder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface TradeMarketService {
    ResponseJson CreateOrder(TradeOrder tradeOrder);

    ResponseJson CancelOrder( int tradeId ,int userId);

    ResponseJson GetOrders(String userId);

    ResponseJson GetOrdersByType(String userId, String tradeType );

    ResponseJson UpdateOrder(int tradeId);

    ResponseJson getOrdersByStatus(int userId,int status);
     ResponseJson getExamineOrders(int userId);

    ResponseJson updateStatus2(int tradeId);
}
