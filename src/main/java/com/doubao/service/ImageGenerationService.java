package com.doubao.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@Service
public class ImageGenerationService {

    /**
     * 生成简单的示例图片（模拟AI生成的图片）
     */
    public String generateSampleImage(String prompt) throws IOException {
        int width = 300;
        int height = 200;

        // 创建BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景色（根据提示词生成不同颜色）
        Color bgColor = getColorFromPrompt(prompt);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);

        // 添加文字
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String text = "豆包AI生成";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (width - textWidth) / 2, height / 2);

        // 添加提示词相关信息
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String promptText = "主题: " + (prompt.length() > 20 ? prompt.substring(0, 20) + "..." : prompt);
        int promptWidth = g2d.getFontMetrics().stringWidth(promptText);
        g2d.drawString(promptText, (width - promptWidth) / 2, height / 2 + 30);

        // 添加一些图形元素
        g2d.setColor(new Color(255, 255, 255, 128));
        g2d.fillOval(50, 50, 60, 60);
        g2d.setColor(new Color(255, 200, 100, 128));
        g2d.fillRect(width - 110, 50, 60, 60);

        g2d.dispose();

        // 转换为Base64
        return imageToBase64(image);
    }

    /**
     * 根据提示词生成颜色
     */
    private Color getColorFromPrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return new Color(70, 130, 180); // 默认蓝色
        }

        String lowerPrompt = prompt.toLowerCase();
        Random random = new Random(prompt.hashCode());

        if (lowerPrompt.contains("天空") || lowerPrompt.contains("蓝色")) {
            return new Color(135, 206, 235); // 天蓝色
        } else if (lowerPrompt.contains("自然") || lowerPrompt.contains("绿色")) {
            return new Color(34, 139, 34); // 森林绿
        } else if (lowerPrompt.contains("日落") || lowerPrompt.contains("橙色")) {
            return new Color(255, 140, 0); // 深橙色
        } else if (lowerPrompt.contains("夜晚") || lowerPrompt.contains("黑色")) {
            return new Color(25, 25, 112); // 午夜蓝
        } else {
            // 随机但基于提示词hash的确定性颜色
            return new Color(
                    Math.abs(random.nextInt() % 156) + 50,  // R: 50-205
                    Math.abs(random.nextInt() % 156) + 50,  // G: 50-205
                    Math.abs(random.nextInt() % 156) + 50   // B: 50-205
            );
        }
    }

    /**
     * 将BufferedImage转换为Base64字符串
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 生成错误图片
     */
    public String generateErrorImage(String errorMessage) throws IOException {
        int width = 300;
        int height = 150;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 红色错误背景
        g2d.setColor(new Color(255, 200, 200));
        g2d.fillRect(0, 0, width, height);

        // 错误图标
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(50, 50, width - 50, height - 50);
        g2d.drawLine(width - 50, 50, 50, height - 50);

        // 错误信息
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("图片生成失败", width / 2 - 30, 30);
        g2d.drawString(errorMessage.length() > 30 ? errorMessage.substring(0, 30) + "..." : errorMessage,
                width / 2 - 60, height - 20);

        g2d.dispose();

        return imageToBase64(image);
    }
}