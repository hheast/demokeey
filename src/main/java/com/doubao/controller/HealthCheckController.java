package com.doubao.controller;

// HealthCheckController.java


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    private final DataSource dataSource;

    public HealthCheckController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 健康检查端点 - 使用唯一的路径
     */
    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Image Assistant API");
        health.put("timestamp", System.currentTimeMillis());

        // 数据库健康检查
        Map<String, Object> dbHealth = checkDatabaseHealth();
        health.put("database", dbHealth);

        return health;
    }

    /**
     * 详细的系统健康检查 - 使用不同的路径
     */
    @GetMapping("/details")
    public Map<String, Object> detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Image Assistant API");
        health.put("timestamp", System.currentTimeMillis());

        // 数据库健康检查
        Map<String, Object> dbHealth = checkDatabaseHealth();
        health.put("database", dbHealth);

        // 系统信息
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("freeMemory", runtime.freeMemory());
        systemInfo.put("totalMemory", runtime.totalMemory());
        systemInfo.put("maxMemory", runtime.maxMemory());
        systemInfo.put("availableProcessors", runtime.availableProcessors());
        health.put("system", systemInfo);

        return health;
    }

    /**
     * 简单的ping端点 - 使用不同的路径
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "pong");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();

        long startTime = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            long responseTime = System.currentTimeMillis() - startTime;
            boolean isValid = connection.isValid(2);

            dbHealth.put("status", isValid ? "UP" : "DOWN");
            dbHealth.put("responseTimeMs", responseTime);
            dbHealth.put("valid", isValid);
            dbHealth.put("product", connection.getMetaData().getDatabaseProductName());

        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
            dbHealth.put("responseTimeMs", System.currentTimeMillis() - startTime);
        }

        return dbHealth;
    }
}