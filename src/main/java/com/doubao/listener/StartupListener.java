package com.doubao.listener;

// StartupListener.java


import org.hibernate.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener {

    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        logger.info("=== 应用启动完成 ===");
        logger.info("Hibernate版本: {}", Version.getVersionString());
        logger.info("Java版本: {}", System.getProperty("java.version"));
        logger.info("操作系统: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
        logger.info("=== 启动完成 ===");
    }
}