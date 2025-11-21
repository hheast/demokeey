package com.doubao.config;

// BeanChecker.java - 检查重复的Bean定义


import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class BeanChecker implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("=== 检查控制器映射 ===");

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

        Set<BeanDefinition> beans = scanner.findCandidateComponents("com.doubao");

        Map<String, Integer> controllerCount = new HashMap<>();

        for (BeanDefinition bean : beans) {
            String className = bean.getBeanClassName();
//            System.out.println("找到控制器: " + className);

            try {
                Class<?> clazz = Class.forName(className);
                String simpleName = clazz.getSimpleName();
                controllerCount.put(simpleName, controllerCount.getOrDefault(simpleName, 0) + 1);
            } catch (ClassNotFoundException e) {
                System.err.println("无法加载类: " + className);
            }
        }

        // 检查重复的控制器名称
//        System.out.println("=== 控制器统计 ===");
        controllerCount.forEach((name, count) -> {
            if (count > 1) {
                System.err.println("⚠️ 发现重复的控制器: " + name + " (出现 " + count + " 次)");
            } else {
                System.out.println("✅ " + name + ": " + count + " 个");
            }
        });
    }
}