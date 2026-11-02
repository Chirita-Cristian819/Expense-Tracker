package com.cristian.expensetracker.emailsendrequests;

import com.cristian.expensetracker.tokens.TokenPurpose;
import com.cristian.expensetracker.users.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "email_send_requests")
public class EmailSendRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_send_request_id")
    private Long emailSendRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "message_id", length = 1000)
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    @NotNull
    private TokenPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private EmailSendStatus status;

    @Column(name = "attempts", nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer attempts;

    @Column(name = "next_attempt_at", nullable = false)
    @NotNull
    private OffsetDateTime nextAttemptAt;

    @Column(name = "last_error", length = 1000)
    @Size(max = 1000)
    private String lastError;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static EmailSendRequestEntity newMailRequest(UserEntity user, TokenPurpose purpose,
                                                        OffsetDateTime nextAttemptAt,
                                                        String messageId) {
        EmailSendRequestEntity request = new EmailSendRequestEntity();
        request.user = user;
        request.purpose = purpose;
        request.status = EmailSendStatus.PENDING;
        request.attempts = 0;
        request.nextAttemptAt = nextAttemptAt;
        request.messageId = messageId;

        return request;
    }
}
