package com.doubao.tool;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageBase64Util {

    /**
     * 图片文件转Base64
     */
    public static String encodeImageToBase64(String imagePath) {
        try {
            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
            return Base64.getEncoder().encodeToString(imageData);
        } catch (Exception e) {
            throw new RuntimeException("图片转Base64失败: " + e.getMessage(), e);
        }
    }

    /**
     * Base64转图片文件
     */
    public static void decodeBase64ToImage(String base64String, String outputPath) {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64String);
            Files.write(Paths.get(outputPath), imageData);
        } catch (Exception e) {
            throw new RuntimeException("Base64转图片失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查文件是否为图片
     */
    public static boolean isImageFile(String filename) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        String lowerFilename = filename.toLowerCase();

        for (String ext : imageExtensions) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}