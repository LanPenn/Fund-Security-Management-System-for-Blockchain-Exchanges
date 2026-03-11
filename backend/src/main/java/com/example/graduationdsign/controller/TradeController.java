package com.example.graduationdsign.controller;

import com.example.graduationdsign.dao.TradeMarketMapper;
import com.example.graduationdsign.entity.TradeOrder;
import com.example.graduationdsign.entity.ResponseJson;
import com.example.graduationdsign.service.TradeMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/Trade")
public class TradeController {

    @Autowired
    TradeMarketService tradeMarketService;
    @Autowired
    ResponseJson responseJson;

    @PostMapping("/CreateOrder")
    public ResponseJson CreateOrder(@RequestBody TradeOrder tradeOrder){
        System.out.println(tradeOrder);
        return tradeMarketService.CreateOrder(tradeOrder);
    }

    @DeleteMapping("/CancelOrder")
    public ResponseJson CancelOrder(@RequestParam int tradeId,@RequestParam int userId){
        System.out.println("dwad"+tradeId);
        return tradeMarketService.CancelOrder(tradeId,userId);
    }

    @PostMapping("/GetOrders")
    public ResponseJson GetOrders(@RequestParam String userId ){
        return tradeMarketService.GetOrders(userId);
    }

    @PostMapping("/GetOrdersByType")
    public ResponseJson GetOrdersByType(@RequestParam String userId ,@RequestParam String tradeType){
        return tradeMarketService.GetOrdersByType(userId,tradeType);
    }
    @PostMapping("/UpdateOrder")
    public ResponseJson UpdateOrder(@RequestParam int tradeId){
        return tradeMarketService.UpdateOrder(tradeId);
    }

    @PostMapping("/UpdateOrder2")
    public ResponseJson UpdateOrder2(@RequestParam int tradeId){

        return  tradeMarketService.updateStatus2(tradeId);

    }
    @PostMapping("/getOrdersByStatus")
    public ResponseJson getOrdersByStatus(@RequestParam int userId,@RequestParam int status){
        return tradeMarketService.getOrdersByStatus(userId,status);
    }

    @PostMapping("/getExamineOrders")
    public ResponseJson getExamineOrders(@RequestParam int userId){
        return tradeMarketService.getExamineOrders(userId);
    }


}
