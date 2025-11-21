package com.doubao.service;

// SimpleAuthService.java


import com.doubao.model.User;
import com.doubao.repository.UserRepository;
import com.doubao.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleAuthService {

    @Autowired
    private UserRepository userRepository;

    // 简单的Token存储（生产环境应使用Redis或数据库）
    private java.util.Map<String, Long> tokenStore = new java.util.concurrent.ConcurrentHashMap<>();

    public User register(String username, String email, String password) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setVipLevel(0);
        user.setVipPoints(0);

        return userRepository.save(user);
    }

    public String login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();

        // 验证密码
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成简单Token
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getId());

        return token;
    }

    public User validateToken(String token) {
        if (token == null || !tokenStore.containsKey(token)) {
            throw new RuntimeException("无效的Token");
        }

        Long userId = tokenStore.get(token);
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            tokenStore.remove(token);
            throw new RuntimeException("用户不存在");
        }

        return userOptional.get();
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }
}