// service/ChatService.java
package com.doubao.service;


// ChatService.java


import com.doubao.model.ChatMessage;
import com.doubao.model.ChatSession;
import com.doubao.model.Message;
import com.doubao.repository.ChatSessionRepository;
import com.doubao.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ChatService {
    @Autowired
    private ImageGenerationService imageGenerationService;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FileStorageService fileStorageService;
    private final Map<String, List<ChatMessage>> chatSessions = new ConcurrentHashMap<>();
    /**
     * 创建聊天会话
     */
    public ChatSession createChatSession(Long userId, String title) {
        if (title == null || title.trim().isEmpty()) {
            title = "新对话 " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }

        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        return chatSessionRepository.save(session);
    }

    /**
     * 发送消息
     */


    public String generateAIResponse(String userMessage, boolean hasImage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "您好！请问有什么我可以帮助您的吗？";
        }

        String lowerMessage = userMessage.toLowerCase();

        // 检查是否需要生成图片
        if (shouldGenerateImage(lowerMessage)) {
            try {
                String imageData = imageGenerationService.generateSampleImage(userMessage);
                return "IMAGE_GENERATED:" + imageData;
            } catch (IOException e) {
                return "抱歉，图片生成失败：" + e.getMessage();
            }
        }

        // 原有的文本回复逻辑
        Map<String, String> responses = new HashMap<>();
        responses.put("生成图片", "好的，我来为您生成一张图片！");
        responses.put("画图", "正在为您创作...");
        responses.put("图片", "您想要什么风格的图片呢？");
        responses.put("你好", "你好！我是豆包智能助手，很高兴为您服务！");
        responses.put("嗨", "嗨！今天有什么可以帮您的吗？");
        responses.put("您好", "您好！我是豆包，您的智能助手。");
        responses.put("你是谁", "我是豆包智能助手，一个基于人工智能的聊天机器人。");
        responses.put("你会做什么", "我可以回答您的问题、生成图片、帮助您解决问题等。");

        for (Map.Entry<String, String> entry : responses.entrySet()) {
            if (lowerMessage.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }

        // 针对图片消息的特殊处理
        if (hasImage) {
            return "感谢您分享的图片！这张图片很精彩！";
        }

        // 默认回复
        String[] defaultReplies = {
                "这是一个很有趣的话题！",
                "我明白了，让我想想如何最好地帮助您。",
                "这个问题很有意思！",
                "感谢您的提问！",
                "我理解您的意思。"
        };

        Random random = new Random();
        return defaultReplies[random.nextInt(defaultReplies.length)];
    }


    private boolean shouldGenerateImage(String message) {
        String[] imageKeywords = {
                "生成图片", "画图", "创作图片", "生成图像", "制作图片",
                "generate image", "draw", "create picture", "make image"
        };

        for (String keyword : imageKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取聊天历史
     */
    public List<Message> getChatHistory(Long sessionId, Long userId) {
        // 验证会话存在且属于当前用户
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("聊天会话不存在");
        }

        ChatSession session = sessionOpt.get();
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此聊天会话");
        }

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    /**
     * 获取用户的聊天会话列表
     */
    public List<ChatSession> getUserChatSessions(Long userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    /**
     * 删除聊天会话
     */
    public void deleteChatSession(Long sessionId, Long userId) {
        // 验证会话存在且属于当前用户
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("聊天会话不存在");
        }

        ChatSession session = sessionOpt.get();
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此聊天会话");
        }

        // 删除消息
        messageRepository.deleteBySessionId(sessionId);
        // 删除会话
        chatSessionRepository.deleteById(sessionId);
    }

    /**
     * 更新会话标题
     */
    public ChatSession updateSessionTitle(Long sessionId, Long userId, String title) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("聊天会话不存在");
        }

        ChatSession session = sessionOpt.get();
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此聊天会话");
        }

        session.setTitle(title);
        session.setUpdatedAt(LocalDateTime.now());

        return chatSessionRepository.save(session);
    }

    /**
     * 获取会话详情
     */
    public ChatSession getSessionDetails(Long sessionId, Long userId) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("聊天会话不存在");
        }

        ChatSession session = sessionOpt.get();
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此聊天会话");
        }

        return session;
    }

    /**
     * 生成AI回复
     */


    public void saveMessage(String sessionId, String type, String content, String imageData) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "default_session";
        }

        List<ChatMessage> messages = chatSessions.getOrDefault(sessionId, new ArrayList<>());

        ChatMessage message = new ChatMessage(
                System.currentTimeMillis(),
                sessionId,
                type,
                content,
                imageData,
                LocalDateTime.now()
        );

        messages.add(message);

        // 限制消息数量
        if (messages.size() > 100) {
            messages = messages.subList(messages.size() - 50, messages.size());
        }

        chatSessions.put(sessionId, messages);

        System.out.println("保存消息 - 会话: " + sessionId +
                ", 类型: " + type +
                ", 内容: " + (content != null ? content.substring(0, Math.min(50, content.length())) : "[图片]"));
    }

}