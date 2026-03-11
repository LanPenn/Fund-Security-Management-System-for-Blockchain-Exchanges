package com.example.graduationdsign.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduationdsign.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PathVariable;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    @Update("update account set bau = bau + #{value} where userid = #{id}")
    int updateBalance(@Param("id") int id, @Param("value") int value);

    @Update("update account set bae = bae + #{value} where userid = #{id}")
    int updateETH(@Param("id") int id, @Param("value") int value);

    @Update("update account set bab = bab + #{value} where userid = #{id}")
    int updateBTC(@Param("id") int id, @Param("value") int value);
    @Select("select * from account where userid= #{userid} ")
    Account checkBalance(@Param("userid") int id);

    @Update("update user set status = #{status} where user_id = #{id}")
    int updateStatus(@Param("id") int id, @Param("status") int status);


}
