package com.isigntech.eventhub.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isigntech.eventhub.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndExpiresAtAfter(String tokenHash, LocalDateTime now);

    void deleteByUser_Id(Long userId);

    void deleteByTokenHash(String tokenHash);
}
