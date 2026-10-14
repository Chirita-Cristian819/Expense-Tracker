package com.cristian.expensetracker.registration.tests;

import com.cristian.expensetracker.registration.RegisterRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterRequestDTOValidationTest {

    private static final String VALID_NAME = "Cristian";
    private static final String VALID_EMAIL = "cristian@example.com";
    private static final String VALID_PASSWORD = "ExamplePassword1";
    private static final String VALID_TIMEZONE = "Europe/Bucharest";

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void validRequestHasNoViolations() {
        assertTrue(invalidFields(request(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)).isEmpty());
    }

    @Test
    void timezoneIsOptional() {
        RegisterRequestDTO request = new RegisterRequestDTO(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, null);
        assertTrue(validator.validate(request).isEmpty());
    }

    // --- displayName ---

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void blankDisplayNameIsRejected(String displayName) {
        assertEquals(Set.of("displayName"), invalidFields(request(displayName, VALID_EMAIL, VALID_PASSWORD)));
    }

    @Test
    void displayNameOf100CharsIsAccepted() {
        assertTrue(invalidFields(request("a".repeat(100), VALID_EMAIL, VALID_PASSWORD)).isEmpty());
    }

    @Test
    void displayNameOf101CharsIsRejected() {
        assertEquals(Set.of("displayName"), invalidFields(request("a".repeat(101), VALID_EMAIL, VALID_PASSWORD)));
    }

    // --- email ---

    @ParameterizedTest
    @ValueSource(strings = {"", "not-an-email", "cristian@", "@example.com"})
    void invalidEmailIsRejected(String email) {
        assertEquals(Set.of("email"), invalidFields(request(VALID_NAME, email, VALID_PASSWORD)));
    }

    @Test
    void missingEmailIsRejected() {
        assertEquals(Set.of("email"), invalidFields(request(VALID_NAME, null, VALID_PASSWORD)));
    }

    // --- password ---

    @ParameterizedTest
    @ValueSource(strings = {
            "Abcdefgh1",                         // 9 chars: one below the minimum
            "Abcdefghijklmnopqrstuvwxyz1234X",   // 31 chars: one above the maximum
            "abcdefghij1",                       // no uppercase letter
            "ABCDEFGHIJ1",                       // no lowercase letter
            "Abcdefghijk",                       // no digit
            ""
    })
    void invalidPasswordIsRejected(String password) {
        assertEquals(Set.of("password"), invalidFields(request(VALID_NAME, VALID_EMAIL, password)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Abcdefghi1",                        // 10 chars: exactly the minimum
            "Abcdefghijklmnopqrstuvwxyz1234",    // 30 chars: exactly the maximum
            "Abc!@#$%^&*1"                       // special characters are allowed
    })
    void validPasswordIsAccepted(String password) {
        assertTrue(invalidFields(request(VALID_NAME, VALID_EMAIL, password)).isEmpty());
    }

    private RegisterRequestDTO request(String displayName, String email, String password) {
        return new RegisterRequestDTO(displayName, email, password, VALID_TIMEZONE);
    }

    private Set<String> invalidFields(RegisterRequestDTO request) {
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(request);
        return violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
