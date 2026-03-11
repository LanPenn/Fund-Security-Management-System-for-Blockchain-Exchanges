package com.example.graduationdsign.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduationdsign.entity.tradeAlert;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface alterMapper extends BaseMapper<tradeAlert> {


    // 根据警报ID删除警报记录
    @Delete("DELETE FROM alert WHERE alert_id = #{alertId}")
    int deleteAlertById(@Param("alertId") Integer alertId);

    // 更新警报记录
    @Update("UPDATE alert SET user_id = #{userId}, alert_type = #{alertType}, " +
            "description = #{description}, create_time = #{createTime}, trade_id = #{tradeId} " +
            "WHERE alert_id = #{alertId}")
    void updateAlert(tradeAlert tradeAlert);

    // 查询所有警报记录
    @Select("SELECT * FROM alert")
    List<tradeAlert> selectAllAlerts();

}
