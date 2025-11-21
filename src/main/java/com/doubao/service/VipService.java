package com.doubao.service;

// VipService.java


import com.doubao.model.PointRecord;
import com.doubao.model.User;
import com.doubao.repository.PointRecordRepository;
import com.doubao.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VipService {

    private final UserRepository userRepository;
    private final PointRecordRepository pointRecordRepository;

    public VipService(UserRepository userRepository, PointRecordRepository pointRecordRepository) {
        this.userRepository = userRepository;
        this.pointRecordRepository = pointRecordRepository;
    }

    @Transactional
    public User addPoints(Long userId, Integer points, String changeType, String description) {
        // 查找用户
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();

        // 更新用户积分
        user.setVipPoints(user.getVipPoints() + points);

        // 更新VIP等级（示例规则：每1000积分升一级）
        int newLevel = user.getVipPoints() / 1000;
        if (newLevel > user.getVipLevel()) {
            user.setVipLevel(newLevel);
        }

        // 保存用户信息
        user = userRepository.save(user);

        // 创建积分记录
        PointRecord pointRecord = new PointRecord(user, points, changeType, description);
        pointRecordRepository.save(pointRecord);

        return user;
    }

    @Transactional(readOnly = true)
    public List<PointRecord> getPointHistory(Long userId) {
        return pointRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Integer getTotalPoints(Long userId) {
        Integer total = pointRecordRepository.getTotalPointsByUserId(userId);
        return total != null ? total : 0;
    }

    @Transactional
    public User deductPoints(Long userId, Integer points, String changeType, String description) {
        // 查找用户
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();

        // 检查积分是否足够
        if (user.getVipPoints() < points) {
            throw new RuntimeException("积分不足");
        }

        // 扣除积分
        user.setVipPoints(user.getVipPoints() - points);

        // 保存用户信息
        user = userRepository.save(user);

        // 创建积分记录（负值表示扣除）
        PointRecord pointRecord = new PointRecord(user, -points, changeType, description);
        pointRecordRepository.save(pointRecord);

        return user;
    }
}