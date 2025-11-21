package com.doubao.config;

// MappingConflictChecker.java



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MappingConflictChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MappingConflictChecker.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public MappingConflictChecker(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("开始检查URL映射...");

            Map<RequestMappingInfo, org.springframework.web.method.HandlerMethod> handlerMethods =
                    requestMappingHandlerMapping.getHandlerMethods();

            List<String> mappings = new ArrayList<>();

            for (Map.Entry<RequestMappingInfo, org.springframework.web.method.HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                org.springframework.web.method.HandlerMethod handlerMethod = entry.getValue();

                // 安全地获取映射信息
                String mapping = getMappingInfo(mappingInfo, handlerMethod);
                if (mapping != null) {
                    mappings.add(mapping);
                }
            }

            // 打印所有映射
            logger.info("=== 发现 {} 个URL映射 ===", mappings.size());
            for (String mapping : mappings) {
                logger.info("   {}", mapping);
            }

            logger.info("✅ URL映射检查完成");

        } catch (Exception e) {
            logger.error("❌ URL映射检查失败: {}", e.getMessage());
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 安全地获取映射信息
     */
    private String getMappingInfo(RequestMappingInfo mappingInfo, org.springframework.web.method.HandlerMethod handlerMethod) {
        try {
            StringBuilder mapping = new StringBuilder();

            // 获取HTTP方法
            if (mappingInfo.getMethodsCondition() != null) {
                Set<org.springframework.web.bind.annotation.RequestMethod> methods =
                        mappingInfo.getMethodsCondition().getMethods();
                if (methods != null && !methods.isEmpty()) {
                    mapping.append(methods).append(" ");
                } else {
                    mapping.append("[ANY] ");
                }
            } else {
                mapping.append("[ANY] ");
            }

            // 安全地获取路径模式
            PatternsRequestCondition patternsCondition = mappingInfo.getPatternsCondition();
            if (patternsCondition != null) {
                Set<String> patterns = patternsCondition.getPatterns();
                if (patterns != null && !patterns.isEmpty()) {
                    mapping.append(patterns).append(" ");
                } else {
                    mapping.append("[/**] ");
                }
            } else {
                // 尝试其他方式获取路径
                mapping.append(getAlternativePathInfo(mappingInfo)).append(" ");
            }

            // 添加处理器信息
            mapping.append("-> ")
                    .append(handlerMethod.getBeanType().getSimpleName())
                    .append(".")
                    .append(handlerMethod.getMethod().getName());

            return mapping.toString();

        } catch (Exception e) {
            logger.warn("无法解析映射信息: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 备选方法获取路径信息
     */
    private String getAlternativePathInfo(RequestMappingInfo mappingInfo) {
        try {
            // 尝试反射获取路径信息
            java.lang.reflect.Field patternsField = mappingInfo.getClass().getDeclaredField("patternsCondition");
            patternsField.setAccessible(true);
            PatternsRequestCondition patterns = (PatternsRequestCondition) patternsField.get(mappingInfo);

            if (patterns != null) {
                Set<String> patternSet = patterns.getPatterns();
                if (patternSet != null && !patternSet.isEmpty()) {
                    return patternSet.toString();
                }
            }
        } catch (Exception e) {
            // 忽略反射异常
        }

        return "[未知路径]";
    }
}