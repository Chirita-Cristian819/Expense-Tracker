package com.cristian.expensetracker.emailsendrequests;

import com.cristian.expensetracker.emailsendrequests.utils.EmailSender;
import com.cristian.expensetracker.tokens.AuthTokenService;
import com.cristian.expensetracker.tokens.TokenPurpose;
import com.cristian.expensetracker.users.UserEntity;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class EmailSendRequestService {

    private static final Logger log = LoggerFactory.getLogger(EmailSendRequestService.class);

    private final EmailSendRequestRepository emailSendRequestRepository;
    private final EmailSender emailSender;
    private final EmailSendRequestProcessor emailSendRequestProcessor;

    public EmailSendRequestService(EmailSendRequestRepository emailSendRequestRepository,
                                   EmailSender emailSender,
                                   SecureRandom secureRandom,
                                   AuthTokenService authTokenService, EmailSendRequestProcessor emailSendRequestProcessor) {
        this.emailSendRequestRepository = emailSendRequestRepository;
        this.emailSender = emailSender;
        this.emailSendRequestProcessor = emailSendRequestProcessor;
    }

    public List<EmailSendRequestEntity> findWaitingEmailSendRequests(){
        return emailSendRequestRepository.findPendingRequests(
                EmailSendStatus.PENDING,
                OffsetDateTime.now(ZoneOffset.UTC),
                Limit.of(50));
    }

    public void createEmailSendRequestIfNonePending(UserEntity user, TokenPurpose purpose) {
        if (emailSendRequestRepository.existsByUserAndPurposeAndStatus(user, purpose, EmailSendStatus.PENDING)) {
            return;
        }
        emailSendRequestRepository.save(
                EmailSendRequestEntity.newMailRequest(user, purpose, OffsetDateTime.now(ZoneOffset.UTC), null));
    }

    /*
    * Scheduled job implements the following rule:
     * All users with pending emails are queried in the email_send_requests table
     * 1. A new token entity is generated
     * 2. Verification email with link is sent
     * 3. Previously generated email tokens are checked and deactivated (excluding the latest generated one)
     * 4. The email send status is updated to SENT if the operation completes, FAILED if more than 5 attempts fail
     */
    @Scheduled(fixedDelay = 5000)
    public void checkForPendingEmailsAndSendVerificationEmail() {
        List<Long> pendingRequestIds = findWaitingEmailSendRequests().stream()
                .map(EmailSendRequestEntity::getEmailSendRequestId)
                .toList();

        CircuitBreaker emailCircuitBreaker = emailSender.getEmailCircuitBreaker();

        for (Long requestId : pendingRequestIds) {
            if (!EnumSet.of(CircuitBreaker.State.CLOSED, CircuitBreaker.State.HALF_OPEN)
                    .contains(emailCircuitBreaker.getState())) {
                break;
            }
            try {
                emailSendRequestProcessor.processVerificationTokenCreationAndEmailSending(requestId, emailCircuitBreaker);
            } catch (RuntimeException exception) {
                // only this row's transaction is rolled back; continue with the next rows
                log.error("Unexpected error while processing email send request {}", requestId, exception);
            }
        }
    }


}
