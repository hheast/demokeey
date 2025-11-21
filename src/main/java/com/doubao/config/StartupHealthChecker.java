package com.doubao.config;

// StartupHealthChecker.java - 简化版


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class StartupHealthChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupHealthChecker.class);

    private final DataSource dataSource;

    public StartupHealthChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("🔍 开始应用健康检查...");

        // 检查数据库连接
        checkDatabaseConnection();

        // 检查系统资源
        checkSystemResources();

        logger.info("✅ 应用健康检查完成");
    }

    private void checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(2);
            if (isValid) {
                logger.info("✅ 数据库连接正常");
            } else {
                logger.warn("⚠️ 数据库连接异常");
            }
        } catch (Exception e) {
            logger.error("❌ 数据库连接失败: {}", e.getMessage());
        }
    }

    private void checkSystemResources() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;

        logger.info("💾 内存使用: {}/{}MB (最大: {}MB)", freeMemory, totalMemory, maxMemory);
        logger.info("💻 可用处理器: {}", runtime.availableProcessors());
    }
}