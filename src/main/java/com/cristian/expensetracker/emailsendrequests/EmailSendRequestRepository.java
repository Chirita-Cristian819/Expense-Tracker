package com.cristian.expensetracker.emailsendrequests;

import com.cristian.expensetracker.tokens.TokenPurpose;
import com.cristian.expensetracker.users.UserEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailSendRequestRepository extends JpaRepository<EmailSendRequestEntity, Long> {
    @Query("""
        SELECT r FROM EmailSendRequestEntity r
        WHERE r.status = :status
          AND r.nextAttemptAt <= :now
          AND r.attempts < 5
        ORDER BY r.nextAttemptAt ASC
        """)
    List<EmailSendRequestEntity> findPendingRequests(@Param("status") EmailSendStatus status,
                                                     @Param("now") OffsetDateTime now,
                                                     Limit limit);

    @Modifying
    @Query("""
        UPDATE EmailSendRequestEntity r
        SET r.status = :verified
        WHERE r.status = :sent
          AND r.sentAt <= :cutoff
        """)
    int markAsVerified(@Param("sent") EmailSendStatus sent,
                       @Param("verified") EmailSendStatus verified,
                       @Param("cutoff") OffsetDateTime cutoff);

    boolean existsByUserAndPurposeAndStatus(UserEntity user, TokenPurpose purpose, EmailSendStatus status);
    Optional<EmailSendRequestEntity> findByMessageId(String trim);
}
