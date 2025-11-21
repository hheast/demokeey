package com.doubao.controller;
// DatabaseInfoController.java - 最终修复版


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DatabaseInfoController {

    private final DataSource dataSource;

    public DatabaseInfoController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/database-info")
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            info.put("databaseProduct", metaData.getDatabaseProductName());
            info.put("databaseVersion", metaData.getDatabaseProductVersion());
            info.put("driverName", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            info.put("url", metaData.getURL());
            info.put("username", metaData.getUserName());
            info.put("maxConnections", metaData.getMaxConnections());
            info.put("defaultTransactionIsolation", metaData.getDefaultTransactionIsolation());

            // 测试连接是否正常工作
            boolean isValid = connection.isValid(5); // 5秒超时
            info.put("connectionValid", isValid);

            info.put("status", "SUCCESS");

        } catch (Exception e) {
            info.put("status", "ERROR");
            info.put("error", e.getMessage());
            info.put("errorType", e.getClass().getName());
        }

        return info;
    }

    @GetMapping("/test-connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();

        long startTime = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            long connectTime = System.currentTimeMillis() - startTime;

            // 执行简单的SQL测试
            boolean canExecuteQuery = connection.createStatement().execute("SELECT 1");

            result.put("status", "SUCCESS");
            result.put("connectionTimeMs", connectTime);
            result.put("canExecuteQuery", canExecuteQuery);
            result.put("message", "数据库连接测试成功");

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            result.put("connectionTimeMs", System.currentTimeMillis() - startTime);
        }

        return result;
    }
}