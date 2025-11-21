package com.doubao.service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtKeyGenerator {

    public static void main(String[] args) {
        try {
            // 生成HS512算法所需的512位（64字节）密钥
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
            keyGen.init(512); // HS512需要512位密钥
            SecretKey secretKey = keyGen.generateKey();

            // 转换为Base64字符串
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            System.out.println("生成的JWT密钥（Base64编码）:");
            System.out.println(base64Key);
            System.out.println("密钥长度: " + base64Key.length() + " 字符");

            // 也可以生成一个简单的长字符串密钥
            String simpleKey = generateSimpleKey(64);
            System.out.println("\n简单的长字符串密钥:");
            System.out.println(simpleKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成指定长度的随机字符串密钥
     */
    public static String generateSimpleKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}