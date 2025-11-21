// DoubaoApplication.java
package com.doubao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DoubaoApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(DoubaoApplication.class, args);
            System.out.println("🚀 应用启动成功！");
            System.out.println("📍 访问地址: http://localhost:8080");
            System.out.println("🔧 健康检查: http://localhost:8080/api/health");
        } catch (Exception e) {
            System.err.println("❌ 应用启动失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}