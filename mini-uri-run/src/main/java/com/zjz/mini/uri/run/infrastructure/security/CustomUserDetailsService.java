package com.zjz.mini.uri.run.infrastructure.security;

import com.zjz.mini.uri.run.domain.dao.UserInfoMapper;
import com.zjz.mini.uri.run.domain.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义UserDetailsService实现
 *
 * @author hkz329
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoMapper userInfoMapper;

    public CustomUserDetailsService(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = findUserByLoginKey(username);
        
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (UserInfo.Status.DISABLED.getCode().equals(userInfo.getStatus())) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        // 根据用户类型设置权限
        String authority = getUserAuthority(userInfo.getUserType());
        
        return User.builder()
                .username(userInfo.getUsername())
                .password("") // 密码由认证服务单独处理
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(authority)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * 根据登录标识查找用户（支持用户名、邮箱、手机号）
     */
    private UserInfo findUserByLoginKey(String loginKey) {
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
     * 根据用户类型获取权限
     */
    private String getUserAuthority(Integer userType) {
        if (UserInfo.UserType.VIP.getCode().equals(userType)) {
            return "ROLE_VIP";
        } else if (UserInfo.UserType.PREMIUM.getCode().equals(userType)) {
            return "ROLE_PREMIUM";
        } else {
            return "ROLE_FREE";
        }
    }
}