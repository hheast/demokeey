package com.doubao.repository;

// UserSessionRepository.java


import com.doubao.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionToken(String sessionToken);

    @Modifying
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < :dateTime")
    void deleteExpiredSessions(@Param("dateTime") LocalDateTime dateTime);

    @Modifying
    @Query("DELETE FROM UserSession us WHERE us.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}