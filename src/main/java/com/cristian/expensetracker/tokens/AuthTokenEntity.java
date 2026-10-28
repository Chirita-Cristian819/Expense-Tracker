package com.cristian.expensetracker.tokens;

import com.cristian.expensetracker.users.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "auth_tokens")
public class AuthTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    @NotNull
    private TokenPurpose purpose;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    @NotNull
    @Size(max = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    @NotNull
    private OffsetDateTime expiresAt;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static AuthTokenEntity newToken(UserEntity user, TokenPurpose purpose,
                                           String tokenHash, OffsetDateTime expiresAt) {

        AuthTokenEntity token = new AuthTokenEntity();
        token.user = user;
        token.purpose = purpose;
        token.tokenHash = tokenHash;
        token.expiresAt = expiresAt;
        token.usedAt = null;

        return token;
    }

}
