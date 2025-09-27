package com.zjz.mini.uri.run.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjz.mini.uri.run.domain.dao.UserAuthMapper;
import com.zjz.mini.uri.run.domain.dao.UserInfoMapper;
import com.zjz.mini.uri.run.domain.dto.RegisterRequest;
import com.zjz.mini.uri.run.domain.entity.UserAuth;
import com.zjz.mini.uri.run.domain.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务
 *
 * @author hkz329
 */
@Service
@Slf4j
public class UserService {

    private final UserInfoMapper userInfoMapper;
    private final UserAuthMapper userAuthMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserInfoMapper userInfoMapper, 
                      UserAuthMapper userAuthMapper,
                      PasswordEncoder passwordEncoder) {
        this.userInfoMapper = userInfoMapper;
        this.userAuthMapper = userAuthMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 根据登录标识查找用户
     */
    public UserInfo findUserByLoginKey(String loginKey) {
        // 先尝试用户名
        UserInfo userInfo = userInfoMapper.selectByUsername(loginKey);
        if (userInfo != null) {
            return userInfo;
        }
        
        // 再尝试邮箱
        if (loginKey.contains("@")) {
            userInfo = userInfoMapper.selectByEmail(loginKey);
            if (userInfo != null) {
                return userInfo;
            }
        }
        
        // 最后尝试手机号
        if (loginKey.matches("^1[3-9]\\d{9}$")) {
            userInfo = userInfoMapper.selectByPhone(loginKey);
            if (userInfo != null) {
                return userInfo;
            }
        }
        
        return null;
    }

    /**
     * 验证用户密码
     */
    public boolean validatePassword(Long userId, String rawPassword) {
        UserAuth userAuth = userAuthMapper.selectByUserIdAndAuthType(userId, UserAuth.AuthType.PASSWORD.getCode());
        if (userAuth == null) {
            return false;
        }
        
        return passwordEncoder.matches(rawPassword, userAuth.getAuthValue());
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userInfoMapper.selectByUsername(username) != null;
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return userInfoMapper.selectByEmail(email) != null;
    }

    /**
     * 检查手机号是否存在
     */
    public boolean existsByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        return userInfoMapper.selectByPhone(phone) != null;
    }

    /**
     * 注册新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public UserInfo registerUser(RegisterRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 检查手机号是否已存在
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 检查密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 创建用户信息
        UserInfo userInfo = new UserInfo()
                .setUsername(request.getUsername())
                .setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername())
                .setEmail(request.getEmail())
                .setPhone(request.getPhone())
                .setStatus(UserInfo.Status.NORMAL.getCode())
                .setUserType(UserInfo.UserType.FREE.getCode());
        
        userInfoMapper.insert(userInfo);
        
        // 创建密码认证信息
        UserAuth userAuth = new UserAuth()
                .setUserId(userInfo.getId())
                .setAuthType(UserAuth.AuthType.PASSWORD.getCode())
                .setAuthKey(request.getUsername())
                .setAuthValue(passwordEncoder.encode(request.getPassword()))
                .setStatus(UserAuth.Status.NORMAL.getCode());
        
        userAuthMapper.insert(userAuth);
        
        log.info("User registered successfully: {}", request.getUsername());
        return userInfo;
    }

    /**
     * 根据用户ID获取用户信息
     */
    public UserInfo getUserById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(UserInfo userInfo) {
        return userInfoMapper.updateById(userInfo) > 0;
    }
}