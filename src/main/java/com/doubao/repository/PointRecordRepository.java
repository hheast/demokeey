package com.doubao.repository;

import com.doubao.model.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    List<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT SUM(pr.pointsChange) FROM PointRecord pr WHERE pr.user.id = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);
}