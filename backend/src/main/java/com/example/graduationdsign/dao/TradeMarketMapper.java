package com.example.graduationdsign.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduationdsign.entity.TradeOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TradeMarketMapper extends BaseMapper<TradeOrder> {
    @Delete("delete from trade where  trade_id = #{tradeId}")
    int deleteByUserId( @Param("tradeId") int tradeId);

    @Select("<script>" +
            "SELECT * FROM trade " +
            "<where>" +
            "   <if test='user_id != 0'>" +
            "       AND user_id = #{user_id}" +
            "   </if>" +
            "</where>" +
            "</script>")
    List<TradeOrder> selectByUserId(@Param("user_id") String user_id);

    @Select("<script>"  +
                        "SELECT * FROM trade" +
                        "<where>" +
                        "trade_type=#{trade_type}" +
                        "   <if test='user_id != 0'>" +
                        "       AND user_id = #{user_id}" +
                       "  </if>" +
                         "AND status=1" +
                        "</where>" +
                       "</script>")
    List<TradeOrder> selectByTradeType(@Param("user_id") String user_id, @Param("trade_type") String trade_type );

    @Select("select username from user where user_id = #{user_id}")
    String selectUsername(@Param("user_id") String user_id);

    @Update("update trade set status = 0 where trade_id = #{tradeId}")
    int updateStatus(@Param("tradeId") int tradeId);

    @Update("update trade set status = 4 where trade_id = #{tradeId}")
    int updateStatus2(@Param("tradeId") int tradeId);

//    @Select("select * from trade where user_id = #{user_id} and status = #{status}")
//    List<TradeOrder> selectOverOrders(@Param("user_id") int user_id ,@Param("status") int status);

    @Select("<script>" +
            "SELECT * FROM trade " +
            "WHERE status = #{status} " +
            "<if test='user_id != -1'> " +
            "  AND user_id = #{user_id} " +
            "</if>" +
            "</script>")
    List<TradeOrder> selectOverOrders(@Param("user_id") int user_id, @Param("status") Integer status);
    @Select("select * from trade where user_id = #{user_id} and status =3")
    List<TradeOrder> selectExamineOrders(@Param("user_id") int user_id);
}
