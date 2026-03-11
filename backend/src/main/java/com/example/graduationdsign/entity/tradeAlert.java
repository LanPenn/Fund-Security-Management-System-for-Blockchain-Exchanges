package com.example.graduationdsign.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("alert")
public class tradeAlert {
    @TableId(type = IdType.AUTO)
    private Integer alertId;
    @TableField("user_id")
    private Integer userId;
    @TableField("alert_type")
    private String alertType;
    private String description;
    private LocalDateTime createTime;
    @TableField("trade_id")
    private Integer tradeId;

    public tradeAlert() {
    }

    public tradeAlert( Integer userId, String alertType, String description, LocalDateTime createTime, Integer tradeId) {
        this.userId = userId;
        this.alertType = alertType;
        this.description = description;
        this.createTime = createTime;
        this.tradeId = tradeId;
    }
}