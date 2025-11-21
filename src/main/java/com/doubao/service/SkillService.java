// service/SkillService.java
package com.doubao.service;

import com.doubao.model.Skill;
import com.doubao.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill getSkillById(String id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("技能不存在: " + id));
    }

    public List<Skill> getSkillsByCategory(String category) {
        return skillRepository.findByCategory(category);
    }
}