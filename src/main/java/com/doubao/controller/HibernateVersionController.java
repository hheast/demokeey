package com.doubao.controller;

// HibernateVersionController.java - 修复版


import org.hibernate.Version;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/version")
public class HibernateVersionController {

    @GetMapping("/hibernate")
    public Map<String, Object> getHibernateVersion() {
        Map<String, Object> versionInfo = new HashMap<>();

        try {
            // 获取Hibernate版本字符串
            String versionString = Version.getVersionString();
            versionInfo.put("hibernateVersion", versionString);

            // 解析版本号（手动解析）
            String[] versionParts = versionString.split("\\.");
            if (versionParts.length >= 2) {
                versionInfo.put("majorVersion", versionParts[0]);
                versionInfo.put("minorVersion", versionParts[1]);
            }

            versionInfo.put("status", "SUCCESS");

        } catch (Exception e) {
            versionInfo.put("status", "ERROR");
            versionInfo.put("error", e.getMessage());
        }

        return versionInfo;
    }
}