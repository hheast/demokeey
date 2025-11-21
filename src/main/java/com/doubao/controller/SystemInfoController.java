package com.doubao.controller;

// SystemInfoController.java


import org.hibernate.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/system")
public class SystemInfoController {

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();

        try {
            // Java环境信息
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("javaVendor", System.getProperty("java.vendor"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            systemInfo.put("userHome", System.getProperty("user.home"));
            systemInfo.put("workingDirectory", System.getProperty("user.dir"));

            // Spring Boot信息
            systemInfo.put("springVersion", getSpringVersion());
            systemInfo.put("springBootVersion", environment.getProperty("spring.boot.version"));
            systemInfo.put("applicationName", environment.getProperty("spring.application.name", "demo_AI"));
            systemInfo.put("serverPort", environment.getProperty("server.port"));
            systemInfo.put("activeProfiles", environment.getActiveProfiles());

            // Hibernate信息
            systemInfo.put("hibernateVersion", Version.getVersionString());

            // 数据库信息
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                systemInfo.put("databaseProduct", metaData.getDatabaseProductName());
                systemInfo.put("databaseVersion", metaData.getDatabaseProductVersion());
                systemInfo.put("driverName", metaData.getDriverName());
                systemInfo.put("driverVersion", metaData.getDriverVersion());
                systemInfo.put("jdbcUrl", metaData.getURL());
            }

            // 系统属性
            systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
            systemInfo.put("freeMemory", Runtime.getRuntime().freeMemory());
            systemInfo.put("totalMemory", Runtime.getRuntime().totalMemory());
            systemInfo.put("maxMemory", Runtime.getRuntime().maxMemory());

            systemInfo.put("status", "SUCCESS");
            systemInfo.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            systemInfo.put("status", "ERROR");
            systemInfo.put("error", e.getMessage());
        }

        return systemInfo;
    }

    private String getSpringVersion() {
        try {
            Package springPackage = org.springframework.core.SpringVersion.class.getPackage();
            return springPackage.getImplementationVersion();
        } catch (Exception e) {
            return "未知";
        }
    }
}