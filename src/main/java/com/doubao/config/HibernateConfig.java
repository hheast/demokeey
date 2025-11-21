package com.doubao.config;


import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            String databaseVersion = metaData.getDatabaseProductVersion();

//            System.out.println("数据库产品: " + databaseProductName);
//            System.out.println("数据库版本: " + databaseVersion);

            // 根据数据库类型和版本设置方言
            if (databaseProductName.toLowerCase().contains("mysql")) {
                if (databaseVersion.startsWith("8")) {
                    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
                } else if (databaseVersion.startsWith("5.7")) {
                    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL57Dialect");
                } else {
                    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
                }
            }
        } catch (Exception e) {
            // 如果无法检测，使用默认的MySQL8方言
            vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
            System.err.println("无法检测数据库版本，使用默认方言: " + e.getMessage());
        }

        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);

        return vendorAdapter;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");
        return properties;
    }
}