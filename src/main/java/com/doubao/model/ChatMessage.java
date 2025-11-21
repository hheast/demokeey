// model/ChatMessage.java
package com.doubao.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ChatMessage {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    private String sessionId;
    private String type; // USER, ASSISTANT
    private String content = "";
    private String skillId;
    private String imageData;
    private List<String> imageUrls = new ArrayList<>();
    private LocalDateTime timestamp;

    public ChatMessage(Long id, String sessionId, String type, String content,
                       String imageData, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.type = type;
        this.content = content;
        this.imageData = imageData;
        this.timestamp = timestamp;
    }

    public enum MessageType {
        USER, ASSISTANT
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }
}