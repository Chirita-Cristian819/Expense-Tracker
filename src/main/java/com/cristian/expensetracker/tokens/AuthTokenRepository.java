package com.cristian.expensetracker.tokens;

import com.cristian.expensetracker.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Long> {
    void deleteByUserAndPurposeAndUsedAtIsNull(UserEntity user, TokenPurpose purpose);
    Optional<AuthTokenEntity> findByTokenHash(String tokenHash);
    boolean existsByTokenHashAndPurposeAndUsedAtIsNullAndExpiresAtAfter(String tokenHash, TokenPurpose purpose, OffsetDateTime now);
}
