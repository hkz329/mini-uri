package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户认证Mapper
 *
 * @author hkz329
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {

    /**
     * 根据认证类型和认证标识查询认证信息
     */
    @Select("SELECT * FROM user_auth WHERE auth_type = #{authType} AND auth_key = #{authKey} AND status = 1")
    UserAuth selectByAuthTypeAndKey(String authType, String authKey);

    /**
     * 根据用户ID查询所有认证方式
     */
    @Select("SELECT * FROM user_auth WHERE user_id = #{userId} AND status = 1")
    List<UserAuth> selectByUserId(Long userId);

    /**
     * 根据用户ID和认证类型查询认证信息
     */
    @Select("SELECT * FROM user_auth WHERE user_id = #{userId} AND auth_type = #{authType} AND status = 1")
    UserAuth selectByUserIdAndAuthType(Long userId, String authType);
}