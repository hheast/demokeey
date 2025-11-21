package com.doubao.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigCheckController {

    @Value("${spring.datasource.url:未设置}")
    private String dbUrl;

    @Value("${jwt.secret:未设置}")
    private String jwtSecret;

    @Value("${server.port:8080}")
    private String serverPort;

    @GetMapping("/check")
    public Map<String, Object> checkConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("status", "OK");
        config.put("databaseUrl", dbUrl.replaceFirst(":[^:]*?@", ":****@"));
        config.put("jwtSecretSet", !"未设置".equals(jwtSecret));
        config.put("serverPort", serverPort);
        config.put("activeProfiles", System.getProperty("spring.profiles.active", "default"));

        return config;
    }
}