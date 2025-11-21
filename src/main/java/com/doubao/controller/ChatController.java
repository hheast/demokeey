// controller/ChatController.java
package com.doubao.controller;

// ChatController.java - 无Security版本



import com.doubao.dto.ChatRequest;
import com.doubao.model.ChatSession;
import com.doubao.model.Message;
import com.doubao.model.User;
import com.doubao.service.AuthService;
import com.doubao.service.ChatService;
import com.doubao.tool.ImageBase64Util;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthService authService;

    /**
     * 从Token中获取用户ID
     */

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody ChatRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
//            System.out.println("收到消息请求 - 内容: " +
//                    (request.getContent() != null ? request.getContent().substring(0, Math.min(50, request.getContent().length())) : "null"));
//            System.out.println("图片数量: " + (request.getImages() != null ? request.getImages().size() : 0));

            // 保存用户消息（支持多图片）
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                // 如果有图片，为每张图片保存一条消息
                for (ChatRequest.ImageData image : request.getImages()) {
                    chatService.saveMessage(
                            request.getSessionId(),
                            "sent",
                            request.getContent() + (request.getContent() != null ? " [图片: " + image.getName() + "]" : "[图片: " + image.getName() + "]"),
                            image.getData()
                    );
                }
            } else {
                // 如果没有图片，保存文本消息
                chatService.saveMessage(
                        request.getSessionId(),
                        "sent",
                        request.getContent(),
                        null
                );
            }

            // 生成AI回复
            boolean hasImages = request.getImages() != null && !request.getImages().isEmpty();
            String aiReply = chatService.generateAIResponse(request.getContent(), hasImages);

            // 检查是否需要生成图片
            String imageData = null;
            String textContent = aiReply;

            if (aiReply.startsWith("IMAGE_GENERATED:")) {
                // 分离图片数据和文本内容
                String[] parts = aiReply.split(":", 2);
                if (parts.length == 2) {
                    imageData = parts[1];
                    textContent = "这是我为您生成的图片：";
                }
            }


            imageData="data:image/png;base64,"+new ImageBase64Util().encodeImageToBase64(uploadDir+"00.jpg");




            // 保存AI回复
            chatService.saveMessage(request.getSessionId(), "received", textContent, imageData);

            // 构建响应
            response.put("success", true);
            response.put("reply", textContent);
            if (imageData != null) {
                response.put("imageData", imageData);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
//            System.err.println("处理消息错误: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("error", "发送消息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }





    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("无效的认证令牌");
        }
        token = token.substring(7);
        User user = authService.validateToken(token);
        return user.getId();
    }



    /**
     * 发送消息（支持文本和图片）
     */

    /**
     * 获取聊天历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        System.out.println(sessionId);
        System.out.println("ffffffffffffff");
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            System.out.println(userId);
            List<Message> messages = chatService.getChatHistory(sessionId, userId);
            System.out.println(messages);
            response.put("success", true);
            response.put("messages", messages);
            response.put("count", messages.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取用户的聊天会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getUserChatSessions(
            @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserIdFromToken(token);
            List<ChatSession> sessions = chatService.getUserChatSessions(userId);
            response.put("success", true);
            response.put("session", sessions);
            response.put("count", sessions.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    /**
     * 创建聊天会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<Map<String, Object>> createChatSession(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String title) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserIdFromToken(token);
            ChatSession session = chatService.createChatSession(userId, title);
            response.put("success", true);
            response.put("message", "聊天会话创建成功");
            response.put("session", session);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    /**
     * 删除聊天会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteChatSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserIdFromToken(token);
            chatService.deleteChatSession(sessionId, userId);

            response.put("success", true);
            response.put("message", "聊天会话删除成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新聊天会话标题
     */
    @PutMapping("/sessions/{sessionId}/title")
    public ResponseEntity<Map<String, Object>> updateSessionTitle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId,
            @RequestParam String title) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            ChatSession updatedSession = chatService.updateSessionTitle(sessionId, userId, title);

            response.put("success", true);
            response.put("message", "会话标题更新成功");
            response.put("session", updatedSession);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSessionDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            ChatSession session = chatService.getSessionDetails(sessionId, userId);

            response.put("success", true);
            response.put("session", session);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}