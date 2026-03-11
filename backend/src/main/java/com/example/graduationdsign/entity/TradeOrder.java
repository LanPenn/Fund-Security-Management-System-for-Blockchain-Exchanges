package com.example.graduationdsign.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("trade")
public class TradeOrder {
    @TableId(type = IdType.AUTO)
    private Integer tradeId;
    @TableField("user_id")
    private String userId;
   private String tradeType;
   private Double money;
   private Double amount;
   private int status;
    LocalDateTime createTime;

    @Override
        public String toString() {
        return "Order{" +
                "tradeId=" + tradeId +
                ", userId=" + userId +
                ", tradeType='" + tradeType + '\'' +
                ", money=" + money +
                ", amount=" + amount +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
