package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.SmsCode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 短信验证码Mapper
 *
 * @author hkz329
 */
@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {

    /**
     * 查询有效的验证码
     */
    @Select("SELECT * FROM sms_code WHERE phone = #{phone} AND code_type = #{codeType} AND used = 0 AND expire_time > NOW() ORDER BY create_time DESC LIMIT 1")
    SmsCode selectValidCode(String phone, String codeType);

    /**
     * 标记验证码为已使用
     */
    @Update("UPDATE sms_code SET used = 1 WHERE id = #{id}")
    int markAsUsed(Long id);

    /**
     * 删除过期验证码
     */
    @Delete("DELETE FROM sms_code WHERE expire_time < NOW()")
    int deleteExpiredCodes();

    /**
     * 查询手机号今日发送次数
     */
    @Select("SELECT COUNT(*) FROM sms_code WHERE phone = #{phone} AND code_type = #{codeType} AND create_time >= CURDATE()")
    int countTodaySendTimes(String phone, String codeType);
}