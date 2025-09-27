package com.zjz.mini.uri.run.domain.service;

import com.zjz.mini.uri.run.domain.dao.UserSessionMapper;
import com.zjz.mini.uri.run.domain.dto.AuthResponse;
import com.zjz.mini.uri.run.domain.dto.LoginRequest;
import com.zjz.mini.uri.run.domain.dto.RegisterRequest;
import com.zjz.mini.uri.run.domain.entity.UserInfo;
import com.zjz.mini.uri.run.domain.entity.UserSession;
import com.zjz.mini.uri.run.infrastructure.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 *
 * @author hkz329
 */
@Service
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserSessionMapper userSessionMapper;

    public AuthService(UserService userService,
                      JwtUtil jwtUtil,
                      RedisTemplate<String, Object> redisTemplate,
                      UserSessionMapper userSessionMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.userSessionMapper = userSessionMapper;
    }

    /**
     * 用户登录
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // 查找用户
        UserInfo userInfo = userService.findUserByLoginKey(request.getLoginKey());
        if (userInfo == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
//        if (!userService.validatePassword(userInfo.getId(), request.getPassword())) {
//            throw new RuntimeException("密码错误");
//        }

        // 生成令牌
        String accessToken = jwtUtil.generateAccessToken(userInfo.getId(), userInfo.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userInfo.getId(), userInfo.getUsername());

        // 创建会话记录
        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession()
                .setUserId(userInfo.getId())
                .setSessionId(sessionId)
                .setDeviceInfo(getDeviceInfo(httpRequest))
                .setIpAddress(getClientIpAddress(httpRequest))
                .setUserAgent(httpRequest.getHeader("User-Agent"))
                .setExpireTime(LocalDateTime.now().plusSeconds(
                        request.getRememberMe() ? jwtUtil.getRefreshTokenExpire() : jwtUtil.getAccessTokenExpire()));

        userSessionMapper.insert(userSession);

        // 将令牌存储到Redis
        String userTokenKey = "jwt:user:" + userInfo.getUsername();
        redisTemplate.opsForValue().set(userTokenKey, accessToken,
                jwtUtil.getAccessTokenExpire(), TimeUnit.SECONDS);

        String refreshTokenKey = "jwt:refresh:" + userInfo.getId();
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken,
                jwtUtil.getRefreshTokenExpire(), TimeUnit.SECONDS);

        log.info("User login successfully: {}", userInfo.getUsername());

        return buildAuthResponse(accessToken, refreshToken, userInfo);
    }

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        // 注册用户
        UserInfo userInfo = userService.registerUser(request);

        // 自动登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginKey(request.getUsername());
        loginRequest.setPassword(request.getPassword());
        loginRequest.setRememberMe(false);

        return login(loginRequest, httpRequest);
    }

    /**
     * 刷新令牌
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效");
        }

        String tokenType = jwtUtil.getTokenTypeFromToken(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("令牌类型错误");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // 检查刷新令牌是否在Redis中
        String refreshTokenKey = "jwt:refresh:" + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);
        if (!refreshToken.equals(storedToken)) {
            throw new RuntimeException("刷新令牌已失效");
        }

        // 生成新的访问令牌
        String newAccessToken = jwtUtil.generateAccessToken(userId, username);

        // 更新Redis中的访问令牌
        String userTokenKey = "jwt:user:" + username;
        redisTemplate.opsForValue().set(userTokenKey, newAccessToken,
                jwtUtil.getAccessTokenExpire(), TimeUnit.SECONDS);

        UserInfo userInfo = userService.getUserById(userId);
        return buildAuthResponse(newAccessToken, refreshToken, userInfo);
    }

    /**
     * 用户登出
     */
    public void logout(String accessToken, Long userId) {
        if (StringUtils.hasText(accessToken)) {
            // 将访问令牌加入黑名单
            String blacklistKey = "jwt:blacklist:" + accessToken;
            redisTemplate.opsForValue().set(blacklistKey, "1",
                    jwtUtil.getAccessTokenExpire(), TimeUnit.SECONDS);
        }

        if (userId != null) {
            // 删除Redis中的令牌
            UserInfo userInfo = userService.getUserById(userId);
            if (userInfo != null) {
                String userTokenKey = "jwt:user:" + userInfo.getUsername();
                redisTemplate.delete(userTokenKey);
            }

            String refreshTokenKey = "jwt:refresh:" + userId;
            redisTemplate.delete(refreshTokenKey);

            // 删除数据库中的会话记录
            userSessionMapper.deleteByUserId(userId);
        }

        log.info("User logout successfully: userId={}", userId);
    }

    /**
     * 构建认证响应
     */
    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, UserInfo userInfo) {
        AuthResponse.UserInfoVO userInfoVO = new AuthResponse.UserInfoVO()
                .setId(userInfo.getId())
                .setUsername(userInfo.getUsername())
                .setNickname(userInfo.getNickname())
                .setEmail(userInfo.getEmail())
                .setPhone(userInfo.getPhone())
                .setAvatarUrl(userInfo.getAvatarUrl())
                .setUserType(userInfo.getUserType())
                .setExpireTime(userInfo.getExpireTime());

        return new AuthResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtUtil.getAccessTokenExpire())
                .setUserInfo(userInfoVO);
    }

    /**
     * 获取设备信息
     */
    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.hasText(userAgent)) {
            // 简单的设备信息提取
            if (userAgent.contains("Mobile")) {
                return "Mobile";
            } else if (userAgent.contains("Tablet")) {
                return "Tablet";
            } else {
                return "Desktop";
            }
        }
        return "Unknown";
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
