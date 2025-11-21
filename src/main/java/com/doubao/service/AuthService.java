package com.doubao.service;
// AuthService.java - 无Security版本


import com.doubao.model.User;
import com.doubao.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret:default-secret-key-change-this-in-production}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // Token存储（生产环境应使用Redis）
    private Map<String, User> tokenStore = new HashMap<>();

    /**
     * 用户注册
     */
    public User register(String username, String email, String password) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 验证邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashPassword(password));
        user.setVipLevel(0);
        user.setVipPoints(0);

        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    public String login(String username, String password) {
        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();
        // 验证密码
        if (!verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 生成JWT token
        String token = generateJwtToken(user);// 存储token
        tokenStore.put(token, user);
        return token;
    }

    /**
     * 退出登录
     */
    public void logout(String token) {
        tokenStore.remove(token);
    }

    /**
     * 验证Token
     */
    public User validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token不能为空");
        }
        // 验证JWT token
        if (!validateJwtToken(token)) {
            throw new RuntimeException("Token无效或已过期");
        }

        // 从存储中获取用户
        User user = tokenStore.get(token);
        if (user == null) {
            throw new RuntimeException("会话不存在");
        }

        return user;
    }

    /**
     * 获取当前用户
     */
    public User getCurrentUser(String token) {
        return validateToken(token);
    }

    /**
     * 生成JWT Token
     */
//    private String generateJwtToken(User user) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);
//        System.out.println(user.getVipPoints());
//        System.out.println(user.getUsername());
//        System.out.println(user.getPassword());
//        System.out.println(user.getUpdatedAt());
//        System.out.println("user.getUpdatedAt()");
//        System.out.println(user.getId().toString());
//
//        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbb");
//        return Jwts.builder()
//                .setSubject(user.getId().toString())
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }


    public String generateJwtToken(User user) {
        try {
            // 参数验证
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("用户信息不能为空");
            }

            if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT密钥不能为空");
            }

            // 检查密钥长度
            if (jwtSecret.length() < 64) {
                throw new IllegalArgumentException("JWT密钥长度不足，HS512算法需要至少64字符的密钥");
            }

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            // 验证过期时间
            if (expiryDate.before(now)) {
                throw new IllegalArgumentException("JWT过期时间必须晚于当前时间");
            }

            return Jwts.builder()
                    .setSubject(user.getId().toString())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();

        } catch (Exception e) {
            System.out.println(("生成JWT令牌失败: {}"+e.getMessage()+e));
            throw new RuntimeException("生成认证令牌失败", e);
        }
    }

    /**
     * 验证JWT Token
     */
    private boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 密码哈希
     */
    private String hashPassword(String password) {
        try {
            // 简单的SHA-256哈希（生产环境应使用BCrypt）
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }
}