// controller/SkillController.java
package com.doubao.controller;

import com.doubao.dto.ApiResponse;
import com.doubao.model.Skill;
import com.doubao.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @GetMapping("/skills")
    public ApiResponse<List<Skill>> getAllSkills() {
        try {
            List<Skill> skills = skillService.getAllSkills();
            return ApiResponse.success(skills);
        } catch (Exception e) {
            return ApiResponse.error("获取技能列表失败: " + e.getMessage());
        }
    }
}