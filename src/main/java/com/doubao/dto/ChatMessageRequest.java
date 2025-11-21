package com.doubao.dto;

// ChatMessageRequest.java

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ChatMessageRequest {
    private String content;
    private String skill;
    private List<MultipartFile> images;

    // 构造函数
    public ChatMessageRequest() {}

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public List<MultipartFile> getImages() { return images; }
    public void setImages(List<MultipartFile> images) { this.images = images; }
}