package com.cristian.expensetracker.tokens;

import com.cristian.expensetracker.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Long> {
    Optional<AuthTokenEntity> findByTokenHash(String tokenHash);
    boolean existsByTokenHashAndPurposeAndUsedAtIsNullAndExpiresAtAfter(String tokenHash, TokenPurpose purpose, OffsetDateTime now);

    void deleteByUserAndPurposeAndUsedAtIsNullAndTokenIdIsNot(UserEntity user, TokenPurpose tokenPurpose, Long tokenId);
}
