// service/AIService.java
package com.doubao.service;

import com.doubao.model.Skill;
import com.doubao.tool.ImageBase64Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AIService {

    private final Random random = new Random();

    public String generateResponse(String userInput, Skill skill, List<String> imageUrls) {
        // 模拟AI处理延迟
        simulateProcessingDelay();

        String baseResponse;
        if (skill != null) {
            baseResponse = generateSkillBasedResponse(userInput, skill);
        } else {
            baseResponse = generateGeneralResponse(userInput);
        }

        // 如果有图片，在回复中提及
        if (imageUrls != null && !imageUrls.isEmpty()) {
            baseResponse += "\n\n我已经分析了您上传的图片，并结合您的请求提供了上述回答。";
        }

        return baseResponse;
    }

    @Value("${project.upload-path}")
    private String uploadDir;
    private String generateSkillBasedResponse(String userInput, Skill skill) {
        String skillName = skill.getName();
        return switch (skill.getCommand()) {
            case "/summarize" -> "根据您的要求，我对文本进行了摘要：\n\n" +
                    "【摘要结果】\n" + generateSummary(userInput) +
                    "\n\n这是使用" + skillName + "技能生成的结果。";

            case "/translate_en" -> "data:image/jpeg;base64,"+new ImageBase64Util().encodeImageToBase64(uploadDir+"00.jpg");

            case "/translate_zh" -> "【中文翻译】\n" +
                    "翻译结果：" + simulateTranslation(userInput) +
                    "\n\n这是使用" + skillName + "技能翻译的结果。";

            case "/grammar_check" -> "【语法检查】\n" +
                    "检查结果：文本语法基本正确，建议稍作调整以提升表达效果。" +
                    "\n\n这是使用" + skillName + "技能检查的结果。";

            case "/explain_code" -> "【代码解释】\n" +
                    "这段代码的功能是：" + generateCodeExplanation(userInput) +
                    "\n\n这是使用" + skillName + "技能分析的结果。";

            default -> "【" + skillName + "处理结果】\n" +
                    "我已经使用" + skillName + "技能处理了您的请求。处理结果如下：\n\n" +
                    generateMockResponse(userInput) +
                    "\n\n如果您需要进一步调整，请告诉我。";
        };
    }

    private String generateGeneralResponse(String userInput) {
        String[] responses = {
                "您好！我是豆包智能助手，很高兴为您服务。",
                "我已经理解您的需求，正在为您处理...",
                "这是一个很好的问题！让我来帮您分析。",
                "根据您提供的信息，我的建议是：可以先从基础开始，逐步深入。",
                "感谢您的提问！我会尽力提供有用的回答。"
        };

        return responses[random.nextInt(responses.length)] + "\n\n您说：" + userInput;
    }

    private String generateSummary(String text) {
        if (text.length() <= 100) {
            return "文本较短，主要内容为：" + text;
        }
        return "文本主要讨论了几个关键点：首先分析了当前情况，然后提出了解决方案，最后总结了实施建议。";
    }

    private String simulateTranslation(String text) {
        return "这是模拟的翻译结果：" + text.substring(0, Math.min(text.length(), 50)) + "...";
    }

    private String generateCodeExplanation(String code) {
        return "实现了数据处理和业务逻辑功能，包含输入验证、业务处理和结果返回等模块。";
    }

    private String generateMockResponse(String input) {
        return "基于您的输入『" + input + "』，我进行了深入分析并得出了相关结论。这个结果综合考虑了多种因素，力求准确和实用。";
    }

    private void simulateProcessingDelay() {
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3秒延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}