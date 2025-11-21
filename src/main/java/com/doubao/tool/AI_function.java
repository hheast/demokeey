package com.doubao.tool;

import com.doubao.model.Skill;
import org.springframework.beans.factory.annotation.Value;

public class AI_function {


    @Value("${file.upload-dir}")
    private String uploadDir;

    private String generateSkillBasedResponse(String userInput, Skill skill) {
        String skillName = skill.getName();
        return switch (skill.getCommand()) {
            case "/summarize" -> "能生成的结果。";

            case "/translate_en" -> "data:image/jpeg;base64,"+new ImageBase64Util().encodeImageToBase64(uploadDir+"00.jpg");

            case "/translate_zh" -> "技能翻译的结果。";

            case "/grammar_check" -> "【技能检查的结果。";

            case "/explain_code" -> "技能分析的结果。";

            default -> "请告诉我。";
        };
    }
}
