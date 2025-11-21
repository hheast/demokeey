package com.doubao.tool;

public class Example {
    public static void main(String[] args) {
        String imagePath = "photo.jpg";

        // 转换为Base64
        String base64 = ImageBase64Util.encodeImageToBase64(imagePath);
        System.out.println("Base64编码成功，长度: " + base64.length());

        // 转换回来
        ImageBase64Util.decodeBase64ToImage(base64, "photo_copy.jpg");
        System.out.println("图片还原成功");

        // 生成HTML可用的Data URL
        String dataURL = "data:image/jpeg;base64," + base64;
        System.out.println("Data URL前缀: " + dataURL.substring(0, 50) + "...");
    }
}