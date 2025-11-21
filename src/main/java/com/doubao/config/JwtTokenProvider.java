package com.doubao.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * 生成JWT令牌
     */
    public String generateToken(Long userId) {
        try {
            // 参数验证
            if (userId == null) {
                log.warn("生成JWT令牌失败: 用户ID为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            // 获取签名密钥
            SecretKey key = getSigningKey();

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            String token = Jwts.builder()
                    .setSubject(userId.toString())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            log.debug("JWT令牌生成成功，用户ID: {}", userId);
            return token;

        } catch (Exception e) {
            log.error("生成JWT令牌失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成认证令牌失败", e);
        }
    }

    /**
     * 获取签名密钥 - 修复版本
     */
    private SecretKey getSigningKey() {
        try {
            // 如果配置了密钥，使用配置的密钥
            if (jwtSecret != null && !jwtSecret.trim().isEmpty()) {
                // 检查密钥长度
                if (jwtSecret.length() < 64) {
                    log.warn("配置的JWT密钥长度不足（{}字符），HS512算法需要至少64字符，将使用增强密钥", jwtSecret.length());
                    return generateSecureKey();
                }

                // 确保密钥是Base64编码的，如果不是则进行编码
                String base64Key;
                try {
                    // 尝试解码，如果是有效的Base64则使用原值
                    Base64.getDecoder().decode(jwtSecret);
                    base64Key = jwtSecret;
                } catch (IllegalArgumentException e) {
                    // 如果不是Base64，则进行编码
                    base64Key = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
                }

                return Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
            } else {
                log.warn("未配置JWT密钥，将使用自动生成的密钥");
                return generateSecureKey();
            }
        } catch (Exception e) {
            log.error("获取JWT签名密钥失败，使用备用密钥: {}", e.getMessage());
            return generateSecureKey();
        }
    }

    /**
     * 生成安全的密钥
     */
    private SecretKey generateSecureKey() {
        try {
            // 使用JJWT的Keys类生成符合HS512要求的密钥
            SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            log.info("已生成安全的JWT密钥（长度: {}位）", key.getEncoded().length * 8);
            return key;
        } catch (Exception e) {
            log.error("生成安全密钥失败: {}", e.getMessage());
            throw new RuntimeException("无法生成JWT签名密钥", e);
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());
            log.debug("从JWT令牌解析用户ID成功: {}", userId);
            return userId;

        } catch (Exception e) {
            log.error("从JWT令牌获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.debug("JWT令牌验证成功");
            return true;
        } catch (Exception e) {
            log.warn("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成一个安全的JWT密钥（用于配置）
     */
    public static String generateJwtSecret() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        log.info("生成的JWT密钥: {}", base64Key);
        return base64Key;
    }
}