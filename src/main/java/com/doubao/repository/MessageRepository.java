package com.doubao.repository;

// MessageRepository.java - 修复版


import com.doubao.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sessionId = :sessionId")
    Long countBySessionId(@Param("sessionId") Long sessionId);
}