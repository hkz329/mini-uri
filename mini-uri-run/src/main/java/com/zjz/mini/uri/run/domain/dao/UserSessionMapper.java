package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.UserSession;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户会话Mapper
 *
 * @author hkz329
 */
@Mapper
public interface UserSessionMapper extends BaseMapper<UserSession> {

    /**
     * 根据会话ID查询会话信息
     */
    @Select("SELECT * FROM user_session WHERE session_id = #{sessionId} AND expire_time > NOW()")
    UserSession selectBySessionId(String sessionId);

    /**
     * 根据用户ID查询所有有效会话
     */
    @Select("SELECT * FROM user_session WHERE user_id = #{userId} AND expire_time > NOW()")
    List<UserSession> selectValidSessionsByUserId(Long userId);

    /**
     * 删除过期会话
     */
    @Delete("DELETE FROM user_session WHERE expire_time < NOW()")
    int deleteExpiredSessions();

    /**
     * 删除用户的所有会话（用于登出所有设备）
     */
    @Delete("DELETE FROM user_session WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}