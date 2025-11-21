// repository/SkillRepository.java
package com.doubao.repository;

import com.doubao.model.Skill;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SkillRepository {
    private final Map<String, Skill> skills = new ConcurrentHashMap<>();

    public SkillRepository() {
        // 初始化默认技能
        initializeDefaultSkills();
    }

    private void initializeDefaultSkills() {
        List<Skill> defaultSkills = Arrays.asList(
                createSkill("skill_1", "文本摘要", "/summarize", "TEXT", "自动提取文本核心内容"),
                createSkill("skill_2", "英文翻译", "/translate_en", "TEXT", "将中文翻译为英文"),
                createSkill("skill_3", "中文翻译", "/translate_zh", "TEXT", "将英文翻译为中文"),
                createSkill("skill_4", "语法检查", "/grammar_check", "TEXT", "检查并纠正语法错误"),
                createSkill("skill_5", "情感分析", "/sentiment_analysis", "TEXT", "分析文本情感倾向"),
                createSkill("skill_6", "关键词提取", "/extract_keywords", "TEXT", "从文本中提取关键词"),
                createSkill("skill_7", "文本分类", "/text_classification", "TEXT", "对文本进行分类"),
                createSkill("skill_8", "命名实体识别", "/ner", "TEXT", "识别文本中的命名实体"),
                createSkill("skill_9", "文本纠错", "/text_correction", "TEXT", "自动纠正文本错误"),
                createSkill("skill_10", "文本生成", "/text_generation", "TEXT", "根据提示生成文本"),
                createSkill("skill_11", "代码解释", "/explain_code", "CODE", "解释代码功能"),
                createSkill("skill_12", "代码生成", "/generate_code", "CODE", "根据需求生成代码"),
                createSkill("skill_13", "代码优化", "/optimize_code", "CODE", "优化代码性能"),
                createSkill("skill_14", "代码调试", "/debug_code", "CODE", "帮助调试代码问题"),
                createSkill("skill_15", "问答系统", "/qa", "ADVANCED", "智能问答"),
                createSkill("skill_16", "文本相似度", "/text_similarity", "ADVANCED", "计算文本相似度"),
                createSkill("skill_17", "文本聚类", "/text_clustering", "ADVANCED", "对文本进行聚类分析"),
                createSkill("skill_18", "文本去重", "/deduplicate", "ADVANCED", "去除重复文本"),
                createSkill("skill_19", "文本格式化", "/format_text", "ADVANCED", "格式化文本"),
                createSkill("skill_20", "文本加密", "/encrypt_text", "ADVANCED", "加密文本内容")
        );

        defaultSkills.forEach(skill -> skills.put(skill.getId(), skill));
    }

    private Skill createSkill(String id, String name, String command, String category, String description) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(name);
        skill.setCommand(command);
        skill.setCategory(category);
        skill.setDescription(description);
        return skill;
    }

    public List<Skill> findAll() {
        return new ArrayList<>(skills.values());
    }

    public Optional<Skill> findById(String id) {
        return Optional.ofNullable(skills.get(id));
    }

    public List<Skill> findByCategory(String category) {
        return skills.values().stream()
                .filter(skill -> category.equals(skill.getCategory()))
                .toList();
    }
}