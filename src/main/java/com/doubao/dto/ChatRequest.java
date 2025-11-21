package com.doubao.dto;

import java.util.List;

public class ChatRequest {
    private String content;
    private List<ImageData> images; // 改为列表支持多图片
    private String timestamp;
    private String sessionId;

    // 内部类用于存储图片数据
    public static class ImageData {
        private String name;
        private String type;
        private Long size;
        private String data; // Base64数据

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<ImageData> getImages() { return images; }
    public void setImages(List<ImageData> images) { this.images = images; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}