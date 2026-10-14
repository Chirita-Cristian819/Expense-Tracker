package com.cristian.expensetracker.registration;

import com.cristian.expensetracker.currencies.CurrencyRepository;
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

import java.time.ZoneId;
import java.util.Locale;

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

    public RegistrationService(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               CurrencyRepository currencyRepository,
                               AuthenticationManager authenticationManager,
                               UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currencyRepository = currencyRepository;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public static String resolveTimezone(String requested) {
        return ZoneId.getAvailableZoneIds().contains(requested) ? requested : DEFAULT_TIMEZONE;
    }

    public void registerUser(@NonNull RegisterRequestDTO registerRequest) {

        String normalizedEmail = registerRequest.email().trim().toLowerCase(Locale.ROOT);
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        UserEntity checkForExistingEntity = userRepository.findByEmail(normalizedEmail);

        if (checkForExistingEntity == null) {
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
            log.info("Duplicate email registration for user {}", checkForExistingEntity.getUserId());
        }
    }

    public Authentication loginUser(LoginRequestDTO loginRequestDTO){
            String normalizedEmail = loginRequestDTO.email().trim().toLowerCase(Locale.ROOT);
            String userPassword = loginRequestDTO.password();
            Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(normalizedEmail, userPassword));
            userService.checkUserIsEmailVerified(authentication.getName());
            return authentication;
    }

}
