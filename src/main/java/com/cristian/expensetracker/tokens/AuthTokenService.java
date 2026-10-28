package com.cristian.expensetracker.tokens;

import com.cristian.expensetracker.exceptions.InvalidVerificationTokenException;
import com.cristian.expensetracker.users.UserEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;

@Service
public class AuthTokenService {
    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    public void checkUserHasMailVerificationTokensAndDeleteThem(UserEntity user){
        authTokenRepository.deleteByUserAndPurposeAndUsedAtIsNull(user, TokenPurpose.EMAIL_VERIFICATION);
    }

    public void createNewToken(UserEntity user, TokenPurpose tokenPurpose, String generatedToken, OffsetDateTime expiryDate) {
        authTokenRepository.saveAndFlush(
                AuthTokenEntity.newToken(user, tokenPurpose, hashToken(generatedToken), expiryDate)
        );
    }

    public boolean checkTokenIsValid(String token, TokenPurpose tokenPurpose){
        return authTokenRepository.existsByTokenHashAndPurposeAndUsedAtIsNullAndExpiresAtAfter(hashToken(token), tokenPurpose, OffsetDateTime.now(ZoneOffset.UTC));
    }

    public AuthTokenEntity setTokenToUsedAndReturnAuthTokenEntity(String token) {
        AuthTokenEntity tokenEntity = authTokenRepository.findByTokenHash(hashToken(token))
                .orElseThrow(() -> new InvalidVerificationTokenException(TokenPurpose.EMAIL_VERIFICATION, "Token is missing!"));
        tokenEntity.setUsedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return tokenEntity;
    }

    private static String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
