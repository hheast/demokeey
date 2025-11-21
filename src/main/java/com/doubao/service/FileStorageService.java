package com.doubao.service;

// FileStorageService.java - 修复版


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${project.upload-path:./uploads}")
    private String uploadPath;

    @Value("${project.max-file-size:10485760}")
    private long maxFileSize;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
            System.out.println("上传目录创建成功: " + uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("文件大小超过限制");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        Path filePath = Paths.get(uploadPath, fileName);
        Files.copy(file.getInputStream(), filePath);

        return fileName;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return ".dat";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}