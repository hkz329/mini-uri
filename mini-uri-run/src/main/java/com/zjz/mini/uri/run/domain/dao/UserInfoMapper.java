package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户信息Mapper
 *
 * @author hkz329
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 根据用户名查询用户信息
     */
    @Select("SELECT * FROM user_info WHERE username = #{username} AND status = 1")
    UserInfo selectByUsername(String username);

    /**
     * 根据邮箱查询用户信息
     */
    @Select("SELECT * FROM user_info WHERE email = #{email} AND status = 1")
    UserInfo selectByEmail(String email);

    /**
     * 根据手机号查询用户信息
     */
    @Select("SELECT * FROM user_info WHERE phone = #{phone} AND status = 1")
    UserInfo selectByPhone(String phone);
}