package com.example.graduationdsign.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduationdsign.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set password = #{value} where user_id = #{id}")
    int updatePassword(@Param("id") int id, @Param("value") String value);

    @Update("update user set username = #{value} where user_id = #{id}")
    int updateUsername(@Param("id") int id, @Param("value") String value);

    @Update("update user set email = #{value} where user_id = #{id}")
    int updateEmail(@Param("id") int id, @Param("value") String value);

    @Select("select password from user where user_id = #{id}")
    String checkPassword(@Param("id") int id);

    @Delete("delete from user where user_Id= #{id}")
    int updateCancel(@Param("id") int id);

    @Update("update user set status = #{status} where user_id = #{id}")
    int updateStatus(@Param("id") int id, @Param("status") int status);


    @Select("select *  from user where username like CONCAT('%', #{param}, '%')")
    List<User> getUsersByName(@Param("param") String  param);


}
