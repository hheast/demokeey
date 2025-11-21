package com.doubao.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Configuration
public class DatabaseCheckConfig implements ServletContextInitializer {

    @Autowired
    private Environment env;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        try {
            // 获取数据库配置
            String url = env.getProperty("spring.datasource.url");
            String username = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");

            // 尝试建立连接
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");

            Connection connection = DriverManager.getConnection(url, props);
            connection.close();

            System.out.println("数据库连接测试成功！");
        } catch (Exception e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            throw new ServletException("数据库连接失败", e);
        }
    }
}