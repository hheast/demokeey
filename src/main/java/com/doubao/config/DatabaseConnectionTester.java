package com.doubao.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Component
public class DatabaseConnectionTester implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTester.class);

    private final DataSource dataSource;

    public DatabaseConnectionTester(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始测试数据库连接...");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            logger.info("✅ 数据库连接成功!");
            logger.info("数据库产品: {}", metaData.getDatabaseProductName());
            logger.info("数据库版本: {}", metaData.getDatabaseProductVersion());
            logger.info("驱动名称: {}", metaData.getDriverName());
            logger.info("驱动版本: {}", metaData.getDriverVersion());
            logger.info("连接URL: {}", metaData.getURL());

        } catch (Exception e) {
            logger.error("❌ 数据库连接失败: {}", e.getMessage());
            throw new RuntimeException("数据库连接失败: " + e.getMessage(), e);
        }
    }
}