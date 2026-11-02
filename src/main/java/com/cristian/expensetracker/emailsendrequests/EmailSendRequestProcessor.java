package com.cristian.expensetracker.emailsendrequests;

import com.cristian.expensetracker.emailsendrequests.utils.EmailSender;
import com.cristian.expensetracker.tokens.AuthTokenService;
import com.cristian.expensetracker.tokens.TokenPurpose;
import com.cristian.expensetracker.users.UserEntity;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Locale;
import java.util.function.Consumer;

import static com.cristian.expensetracker.config.ResilienceConfig.setCircuitBreakerState;

@Service
public class EmailSendRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(EmailSendRequestProcessor.class);
    private final EmailSender emailSender;
    private final EmailSendRequestRepository emailSendRequestRepository;
    private final AuthTokenService authTokenService;
    private final SecureRandom secureRandom;

    public EmailSendRequestProcessor(EmailSender emailSender,
                                     EmailSendRequestRepository
                                     emailSendRequestRepository,
                                     AuthTokenService authTokenService,
                                     SecureRandom secureRandom) {
        this.emailSender = emailSender;
        this.emailSendRequestRepository = emailSendRequestRepository;
        this.authTokenService = authTokenService;
        this.secureRandom = secureRandom;
    }

    @Transactional
    void processVerificationTokenCreationAndEmailSending(Long requestId, CircuitBreaker emailCircuitBreaker){

        String TOKEN_PATH = "/api/v1/auth/verify-email?token=";
        OffsetDateTime expiryDate =  OffsetDateTime.now(ZoneId.of("UTC")).plusDays(1);

        EmailSendRequestEntity emailSendRequestEntity = emailSendRequestRepository.findById(requestId).orElse(null);
        if (emailSendRequestEntity == null ||
            emailSendRequestEntity.getStatus() != EmailSendStatus.PENDING) {
            return;
        }

        int attempts = emailSendRequestEntity.getAttempts();
        UserEntity user = emailSendRequestEntity.getUser();
        String normalizedEmail = user.getEmail();
        String generatedToken = generateSecureRandomURL();

        Consumer<Exception> setPending = exception -> {
            emailSendRequestEntity.setStatus(EmailSendStatus.PENDING);
            emailSendRequestEntity.setLastError(exception.getMessage());
            emailSendRequestEntity.setNextAttemptAt(
                    OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(2)
            );
        };

        Consumer<Exception> setFailed = exception -> {
            emailSendRequestEntity.setStatus(EmailSendStatus.FAILED);
            emailSendRequestEntity.setLastError(exception.getMessage());
        };


        try {
            Long createdTokenId = authTokenService.createNewToken(user, TokenPurpose.EMAIL_VERIFICATION, generatedToken, expiryDate);
            String messageId = emailSender.sendVerificationEmail(normalizedEmail,
                    TOKEN_PATH + generatedToken);
            authTokenService.checkUserHasMailVerificationTokensAndDeleteThem(user, createdTokenId);
            emailSendRequestEntity.setStatus(EmailSendStatus.SENT);
            emailSendRequestEntity.setSentAt(OffsetDateTime.now(ZoneOffset.UTC));
            emailSendRequestEntity.setMessageId(messageId);
        } catch (CallNotPermittedException exception) {
            log.error("EMAIL SERVICE CIRCUIT BREAKER OPEN!");
        } catch (IOException | MailException | MessagingException exception) {
            switch (classifyFailure(exception)) {
                case CONFIGURATION -> setPending.accept(exception);
                case CREDENTIALS -> {
                    setPending.accept(exception);
                    setCircuitBreakerState(emailCircuitBreaker, CircuitBreaker.State.OPEN, null);
                }
                case QUOTA -> {
                    setPending.accept(exception);
                    setCircuitBreakerState(emailCircuitBreaker, CircuitBreaker.State.OPEN, Duration.ofHours(24));
                }
                case PERMANENT -> setFailed.accept(exception);
                case TEMPORARY -> {
                    int updatedAttempts = attempts + 1;
                    emailSendRequestEntity.setAttempts(updatedAttempts);

                    if (updatedAttempts >= 5) {
                        setFailed.accept(exception);
                    } else {
                        setPending.accept(exception);
                    }
                }
            }
        } catch (RuntimeException exception) {
            log.error("Unexpected error while processing email send request {}", requestId, exception);
        }

    }


    private enum FailureType { CONFIGURATION, PERMANENT, QUOTA, CREDENTIALS, TEMPORARY }

    private static FailureType classifyFailure(Exception exception) {
        /* IO or MailAuthentication errors are part of properties or code configuration and recoverable from the user's perspective
         * Status is left in pending without increasing the number of attempts and reattempted when the configuration is corrected
         * 1. Invalid HTML mail sending template
         * 2. Invalid SMTP credentials on the server
         */
        if (exception instanceof IOException) {
            return FailureType.CONFIGURATION;
        }

        if (exception instanceof MailAuthenticationException) {
            return FailureType.CREDENTIALS;
        }

        /* invalid email address is a permanent failure */
        if (exception instanceof AddressException) {
            return FailureType.PERMANENT;
        }

        /* SMTP 5xx error messages are likely not recoverable and will lead to immediate failure,
         * except Gmail's daily sending limit (550 5.4.5), which resets after 24h
         */
        if (exception instanceof MailSendException sendException) {
            for (Exception failure : sendException.getFailedMessages().values()) {
                int code = smtpReturnCode(failure);
                boolean dailyLimit = failure.getMessage() != null && failure.getMessage().contains("5.4.5");

                if (code >= 500 && code < 600 && !dailyLimit) {
                    return FailureType.PERMANENT;
                } else if (dailyLimit) {
                    return FailureType.QUOTA;
                }
            }
        }

        /* Everything else (SMTP 4xx, connection errors, timeouts, unknown errors)
         * is considered transient and reattempted up to 5 times
         */
        return FailureType.TEMPORARY;
    }

    public static int smtpReturnCode(Exception failure) {
        if (failure instanceof SMTPSendFailedException smtpException) {
            return smtpException.getReturnCode();
        }
        if (failure instanceof SMTPAddressFailedException addressException) {
            return addressException.getReturnCode();
        }
        return -1;
    }


    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String generateSecureRandomURL() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
