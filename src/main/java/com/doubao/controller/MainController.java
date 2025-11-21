package com.doubao.controller;

// MainController.java - 主控制器


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "欢迎使用图片智能助手API");
        response.put("service", "Image Assistant");
        response.put("version", "1.0.0");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", new String[]{
                "GET /api/health - 健康检查",
                "GET /api/test/hello - 测试接口",
                "POST /api/auth/login - 用户登录",
                "POST /api/auth/register - 用户注册",
                "GET /api/chat/sessions - 获取聊天会话"
        });
        return response;
    }
}