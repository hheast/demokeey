// repository/ChatSessionRepository.java
package com.doubao.repository;

// ChatSessionRepository.java - 修复版


import com.doubao.model.ChatSession;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(Long userId);

    @Query("SELECT COUNT(cs) FROM ChatSession cs WHERE cs.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}