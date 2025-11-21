package com.doubao.config;

// DisabledMappingChecker.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DisabledMappingChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DisabledMappingChecker.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("✅ 映射检查已禁用，应用正常启动");
        // 空实现，不执行任何检查
    }
}