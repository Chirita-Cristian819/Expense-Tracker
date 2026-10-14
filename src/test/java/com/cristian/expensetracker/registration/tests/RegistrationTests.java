package com.cristian.expensetracker.registration.tests;

import com.cristian.expensetracker.registration.RegistrationService;
import com.cristian.expensetracker.registration.RegisterRequestDTO;
import com.cristian.expensetracker.currencies.CurrencyEntity;
import com.cristian.expensetracker.currencies.CurrencyRepository;
import com.cristian.expensetracker.users.UserEntity;
import com.cristian.expensetracker.users.UserRepository;
import com.cristian.expensetracker.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationTests {

    private static final String NORMALIZED_EMAIL = "cristian@example.com";
    private static final String RAW_PASSWORD = "ExamplePassword1";
    private static final String ENCODED_PASSWORD = "stubbed-password-hash";
    private static final String TIMEZONE = "Europe/Bucharest";

    private UserRepository userRepository;
    private UserService userService;
    private CurrencyRepository currencyRepository;
    private PasswordEncoder passwordEncoder;
    private RegistrationService service;
    private CurrencyEntity usd;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        currencyRepository = mock(CurrencyRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        service = new RegistrationService(userRepository, passwordEncoder, currencyRepository, authenticationManager, userService);
        usd = new CurrencyEntity();
    }

    @Test
    void newEmailSavesNormalizedUserWithDefaults() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(usd));

        service.registerUser(registrationRequest());

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();
        assertEquals(NORMALIZED_EMAIL, savedUser.getEmail());
        assertEquals(ENCODED_PASSWORD, savedUser.getPasswordHash());
        assertEquals(TIMEZONE, savedUser.getTimezone());
        assertSame(usd, savedUser.getPreferredCurrency());
    }

    @Test
    void existingEmailStillEncodesPasswordButDoesNotSaveUser() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findByEmail(NORMALIZED_EMAIL)).thenReturn(new UserEntity());

        service.registerUser(registrationRequest());
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, never()).saveAndFlush(any(UserEntity.class));
    }

    @Test
    void integrityViolationFromSaveAndFlushIsNotPropagated() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(usd));
        when(userRepository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Simulated unique constraint violation"));

        assertDoesNotThrow(() -> service.registerUser(registrationRequest()));
    }

    @ParameterizedTest
    @CsvSource({
            "Europe/Bucharest, Europe/Bucharest",
            "UTC,              UTC",
            "FakeTimezone,     UTC",
            "+02:00,           UTC",
            ",                 UTC"
    })
    void resolvesKnownTimezoneOrFallsBackToUtc(String input, String expected) {
        assertEquals(expected, RegistrationService.resolveTimezone(input));
    }

    private RegisterRequestDTO registrationRequest() {
        return new RegisterRequestDTO(
                "Cristian", "  CriSTIAN@Example.COM  ", RAW_PASSWORD, TIMEZONE);
    }
}
