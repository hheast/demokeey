package com.doubao.config;

// UrlMappingLogger.java - 简化版


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Component
public class UrlMappingLogger implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UrlMappingLogger.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public UrlMappingLogger(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                    requestMappingHandlerMapping.getHandlerMethods();

            logger.info("🌐 注册了 {} 个URL映射:", handlerMethods.size());

            int count = 0;
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                count++;
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();

                // 安全地记录映射信息
                try {
                    String path = mappingInfo.getPatternsCondition() != null ?
                            mappingInfo.getPatternsCondition().getPatterns().toString() : "[/**]";

                    String methods = mappingInfo.getMethodsCondition() != null ?
                            mappingInfo.getMethodsCondition().getMethods().toString() : "[ANY]";

                    logger.info("   {}. {} {} -> {}.{}",
                            count, methods, path,
                            handlerMethod.getBeanType().getSimpleName(),
                            handlerMethod.getMethod().getName());

                } catch (Exception e) {
                    logger.warn("   无法解析映射 {}: {}", count, e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.warn("无法记录URL映射: {}", e.getMessage());
        }
    }
}