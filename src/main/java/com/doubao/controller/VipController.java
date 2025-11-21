// VipController.java
package com.doubao.controller;

import com.doubao.model.PointRecord;
import com.doubao.model.User;
import com.doubao.service.AuthService;
import com.doubao.service.VipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vip")
@CrossOrigin(origins = "*")
public class VipController {

    private final VipService vipService;
    private final AuthService authService;

    public VipController(VipService vipService, AuthService authService) {
        this.vipService = vipService;
        this.authService = authService;
    }

    @PostMapping("/add-points")
    public ResponseEntity<Map<String, Object>> addPoints(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam Integer points,
            @RequestParam String changeType,
            @RequestParam(required = false) String description) {

        Map<String, Object> response = new HashMap<>();

        if (token == null || !token.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("error", "未提供认证令牌");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            token = token.substring(7);
            User user = authService.validateToken(token);

            User updatedUser = vipService.addPoints(user.getId(), points, changeType, description);

            // 返回用户信息（不包含密码）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", updatedUser.getId());
            userInfo.put("username", updatedUser.getUsername());
            userInfo.put("vipLevel", updatedUser.getVipLevel());
            userInfo.put("vipPoints", updatedUser.getVipPoints());

            response.put("success", true);
            response.put("user", userInfo);
            response.put("message", "积分添加成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/point-history")
    public ResponseEntity<Map<String, Object>> getPointHistory(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || !token.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("error", "未提供认证令牌");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            token = token.substring(7);
            User user = authService.validateToken(token);

            List<PointRecord> history = vipService.getPointHistory(user.getId());

            response.put("success", true);
            response.put("history", history);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getVipInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || !token.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("error", "未提供认证令牌");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            token = token.substring(7);
            User user = authService.validateToken(token);

            Map<String, Object> vipInfo = new HashMap<>();
            vipInfo.put("vipLevel", user.getVipLevel());
            vipInfo.put("vipPoints", user.getVipPoints());
            vipInfo.put("nextLevelPoints", (user.getVipLevel() + 1) * 1000); // 下一级所需积分
            vipInfo.put("pointsToNextLevel", Math.max(0, (user.getVipLevel() + 1) * 1000 - user.getVipPoints()));

            response.put("success", true);
            response.put("vipInfo", vipInfo);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}