package com.zjz.mini.uri.run.infrastructure.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author hkz329
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${miniuri.auth.jwt.secret:miniuri-secret-key-for-jwt-token-generation-2025}")
    private String secret;

    @Value("${miniuri.auth.jwt.access-token-expire:7200}")
    private Long accessTokenExpire; // 访问令牌过期时间（秒），默认2小时

    @Value("${miniuri.auth.jwt.refresh-token-expire:604800}")
    private Long refreshTokenExpire; // 刷新令牌过期时间（秒），默认7天

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "access");
        
        return createToken(claims, accessTokenExpire);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");
        
        return createToken(claims, refreshTokenExpire);
    }

    /**
     * 创建令牌
     */
    private String createToken(Map<String, Object> claims, Long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime * 1000);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            return (Long) userId;
        } catch (Exception e) {
            log.error("Failed to get userId from token", e);
            return null;
        }
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return (String) claims.get("username");
        } catch (Exception e) {
            log.error("Failed to get username from token", e);
            return null;
        }
    }

    /**
     * 从令牌中获取令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return (String) claims.get("tokenType");
        } catch (Exception e) {
            log.error("Failed to get tokenType from token", e);
            return null;
        }
    }

    /**
     * 获取令牌过期时间
     */
    public LocalDateTime getExpirationFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            log.error("Failed to get expiration from token", e);
            return null;
        }
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            LocalDateTime expiration = getExpirationFromToken(token);
            return expiration != null && expiration.isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从令牌中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取访问令牌过期时间（秒）
     */
    public Long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    /**
     * 获取刷新令牌过期时间（秒）
     */
    public Long getRefreshTokenExpire() {
        return refreshTokenExpire;
    }
}