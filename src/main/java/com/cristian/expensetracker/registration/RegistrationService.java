package com.cristian.expensetracker.registration;

import com.cristian.expensetracker.currencies.CurrencyRepository;
import com.cristian.expensetracker.emailsendrequests.EmailSendRequestService;
import com.cristian.expensetracker.exceptions.InvalidVerificationTokenException;
import com.cristian.expensetracker.tokens.AuthTokenService;
import com.cristian.expensetracker.tokens.TokenPurpose;
import com.cristian.expensetracker.users.UserEntity;
import com.cristian.expensetracker.users.UserRepository;
import com.cristian.expensetracker.users.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final String DEFAULT_CURRENCY_CODE = "USD";
    private static final String DEFAULT_TIMEZONE = "UTC";
    private final CurrencyRepository currencyRepository;
    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final EmailSendRequestService emailSendRequestService;

    public RegistrationService(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               CurrencyRepository currencyRepository,
                               AuthenticationManager authenticationManager,
                               UserService userService,
                               AuthTokenService authTokenService,
                               EmailSendRequestService emailSendRequestService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currencyRepository = currencyRepository;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.emailSendRequestService = emailSendRequestService;
    }

    public static String resolveTimezone(String requested) {
        return ZoneId.getAvailableZoneIds().contains(requested) ? requested : DEFAULT_TIMEZONE;
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public void registerUser(@NonNull RegisterRequestDTO registerRequest) {

        String normalizedEmail = registerRequest.email().trim().toLowerCase(Locale.ROOT);
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        Optional<UserEntity> checkForExistingEntity = userRepository.findByEmail(normalizedEmail);

        if (checkForExistingEntity.isEmpty()) {
            try {
                userRepository.saveAndFlush(UserEntity.newUser(registerRequest.displayName(),
                    normalizedEmail,
                    encodedPassword,
                    resolveTimezone(registerRequest.timezone()),
                    currencyRepository.findById(DEFAULT_CURRENCY_CODE).orElseThrow())
                );
            } catch(DataIntegrityViolationException e){
                log.info("Registration attempt for an existing account (concurrent)");
            }

        } else {
            log.info("Duplicate email registration for user {}", checkForExistingEntity.get().getUserId());
        }
    }

    public Authentication loginUser(LoginRequestDTO loginRequestDTO){
            String normalizedEmail = normalizeEmail(loginRequestDTO.email());
            String userPassword = loginRequestDTO.password();
            Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(normalizedEmail, userPassword));
            userService.checkUserIsEmailVerified(authentication.getName());
            return authentication;
    }

    @Transactional
    public void checkEmailIsNotVerifiedAndSendVerificationEmail(EmailVerificationDTO verificationRequest) {
        /* Code implements an outbox pattern, on resend-verification API call, a new entry is created in the email_send_requests table;
        * 1. Check user exists and is not email verified:
        * 2. Insert record in EmailSendRequests table with pending status for current user
        *
        * A scheduled job checkForPendingEmailsAndSendVerificationEmail
        * from EmailSendRequestService class handles token generation and mail sending actions
        * */
        String normalizedEmail = normalizeEmail(verificationRequest.email());
        Optional<UserEntity> existingUser = userRepository.findByEmail(normalizedEmail);

        if (existingUser.isEmpty()) {
            log.info("Verification email requested for a non-existent account");
            return;
        }

        UserEntity user = existingUser.get();
        if (user.getEmailVerifiedAt() != null) {
            log.info("Verification email requested for already verified user {}", user.getUserId());
            return;
        }

        emailSendRequestService.createEmailSendRequestIfNonePending(user, TokenPurpose.EMAIL_VERIFICATION);

    }

    @Transactional
    public void checkVerificationLinkAndVerifyEmail(String token) {
        if(authTokenService.checkTokenIsValid(token, TokenPurpose.EMAIL_VERIFICATION)){
           authTokenService.setTokenToUsedAndReturnAuthTokenEntity(token).
                    getUser().setEmailVerifiedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        } else {
            throw new InvalidVerificationTokenException(TokenPurpose.EMAIL_VERIFICATION, "Email validation link is expired or invalid!");
        }
    }
}
