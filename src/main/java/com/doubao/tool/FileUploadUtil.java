package com.doubao.tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${file.upload-dir:/tmp/uploads}")
    private String uploadDir;

    @Value("${file.max-size:5MB}")
    private String maxSize;

    /**
     * 获取上传目录路径
     */
    public Path getUploadPath() {
        return Paths.get(uploadDir);
    }

    /**
     * 确保上传目录存在
     */
    public void ensureUploadDirExists() throws IOException {
        Path uploadPath = getUploadPath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    /**
     * 保存上传的文件
     */
    public String saveFile(MultipartFile file) throws IOException {
        ensureUploadDirExists();

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        Path filePath = getUploadPath().resolve(uniqueFilename);
        file.transferTo(filePath.toFile());

        return uniqueFilename;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filename) throws IOException {
        Path filePath = getUploadPath().resolve(filename);
        return Files.deleteIfExists(filePath);
    }

    /**
     * 获取文件完整路径
     */
    public String getFileFullPath(String filename) {
        return getUploadPath().resolve(filename).toString();
    }

    // Getter方法
    public String getUploadDir() {
        return uploadDir;
    }

    public String getMaxSize() {
        return maxSize;
    }
}