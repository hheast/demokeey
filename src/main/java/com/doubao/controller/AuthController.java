package com.doubao.controller;

// AuthController.java

import com.doubao.model.User;
import com.doubao.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            String confirmPassword = request.get("confirmPassword");

            // 验证输入
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("用户名不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("邮箱不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("密码不能为空");
            }
            if (!password.equals(confirmPassword)) {
                throw new RuntimeException("密码和确认密码不一致");
            }


            User user = authService.register(username, email, password);

            // 创建响应（不包含密码）
            Map<String, Object> userInfo = createUserInfo(user);

            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = request.get("username");
            String password = request.get("password");
            // 验证输入
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("密码不能为空");
            }

            String token = authService.login(username, password);
            User user = authService.validateToken(token);
            // 创建用户信息响应
            Map<String, Object> userInfo = createUserInfo(user);
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                authService.logout(token);
            }

            response.put("success", true);
            response.put("message", "退出成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || !token.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("error", "未提供认证令牌");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            token = token.substring(7);
            User user = authService.getCurrentUser(token);

            // 创建用户信息响应
            Map<String, Object> userInfo = createUserInfo(user);

            response.put("success", true);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 创建用户信息响应（不包含敏感信息）
     */
    private Map<String, Object> createUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("vipLevel", user.getVipLevel());
        userInfo.put("vipPoints", user.getVipPoints());
        userInfo.put("createdAt", user.getCreatedAt());
        return userInfo;
    }
}