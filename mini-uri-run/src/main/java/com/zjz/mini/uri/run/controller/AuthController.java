package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.run.domain.dto.ApiResponse;
import com.zjz.mini.uri.run.domain.dto.AuthResponse;
import com.zjz.mini.uri.run.domain.dto.LoginRequest;
import com.zjz.mini.uri.run.domain.dto.RegisterRequest;
import com.zjz.mini.uri.run.domain.service.AuthService;
import com.zjz.mini.uri.run.domain.service.UserService;
import com.zjz.mini.uri.run.infrastructure.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author hkz329
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService,
                         UserService userService,
                         JwtUtil jwtUtil) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                          HttpServletRequest httpRequest) {
        try {
            AuthResponse authResponse = authService.login(request, httpRequest);
            return ResponseEntity.ok(ApiResponse.success("登录成功", authResponse));
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getLoginKey(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                             HttpServletRequest httpRequest) {
        try {
            AuthResponse authResponse = authService.register(request, httpRequest);
            return ResponseEntity.ok(ApiResponse.success("注册成功", authResponse));
        } catch (Exception e) {
            log.error("Register failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null) {
                throw new RuntimeException("刷新令牌不能为空");
            }

            AuthResponse authResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", authResponse));
        } catch (Exception e) {
            log.error("Refresh token failed", e);
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long userId = null;
            if (token != null) {
                userId = jwtUtil.getUserIdFromToken(token);
            }

            authService.logout(token, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "登出成功");
            result.put("data", null);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Logout failed", e);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("msg", e.getMessage());
            result.put("data", null);

            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "查询成功");
        result.put("data", Map.of("available", !exists));

        return ResponseEntity.ok(result);
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "查询成功");
        result.put("data", Map.of("available", !exists));

        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                throw new RuntimeException("未找到访问令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("无效的访问令牌");
            }

            var userInfo = userService.getUserById(userId);
            if (userInfo == null) {
                throw new RuntimeException("用户不存在");
            }

            AuthResponse.UserInfoVO userInfoVO = new AuthResponse.UserInfoVO()
                    .setId(userInfo.getId())
                    .setUsername(userInfo.getUsername())
                    .setNickname(userInfo.getNickname())
                    .setEmail(userInfo.getEmail())
                    .setPhone(userInfo.getPhone())
                    .setAvatarUrl(userInfo.getAvatarUrl())
                    .setUserType(userInfo.getUserType())
                    .setExpireTime(userInfo.getExpireTime());

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", userInfoVO);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Get current user failed", e);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("msg", e.getMessage());
            result.put("data", null);

            return ResponseEntity.status(401).body(result);
        }
    }

    /**
     * 从请求中获取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
