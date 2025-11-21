package com.doubao.config;

// JwtConfig.java

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
public class JwtConfig {

    @Value("${jwt.secret:default-secret-key-change-this-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @PostConstruct
    public void init() {
        System.out.println("JWT配置加载: secret=" + (secret != null ? "已设置" : "未设置") + ", expiration=" + expiration);
    }

    public String getSecret() {
        return secret;
    }

    public Long getExpiration() {
        return expiration;
    }
}