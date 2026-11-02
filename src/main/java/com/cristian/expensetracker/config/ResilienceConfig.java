package com.cristian.expensetracker.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.mail.internet.AddressException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;

import java.time.Duration;

import static com.cristian.expensetracker.emailsendrequests.EmailSendRequestProcessor.smtpReturnCode;

@Configuration
public class ResilienceConfig {
    /**
     ** Method used for manual setting of circuit breaker states
     ** It's necessary to open the circuit in cases of deterministic failures such as:
     ** 1. Invalid credentials for for the SMTP email service
     ** 2. Email quota exceeded (a retry cannot happen for at least 24 hours),
     ** prolonging calls while a quota issue exists can determine a longer quota restriction timer
     **/
    public static void setCircuitBreakerState(CircuitBreaker circuit, CircuitBreaker.State state, Duration duration) {
        switch (state) {
            case CLOSED -> circuit.transitionToClosedState();
            case OPEN -> {
                if (duration == null) {
                    circuit.transitionToOpenState();
                } else {
                    circuit.transitionToOpenStateFor(duration);
                }
            }
            case HALF_OPEN -> circuit.transitionToHalfOpenState();
        }
    }

    /**
     * Method created to correctly filter Gmail configuration or service errors from user or code errors
     **/
    public static boolean isRecordableEmailException(Throwable exception) {
        // message could not be built: a bug or bad data, not a server problem
        if (exception instanceof MailParseException || exception instanceof MailPreparationException) {
            return false;
        }

        // invalid email address is not a server issue
        if (exception instanceof AddressException) {
            return false;
        }

        if (exception instanceof MailSendException sendException) {
            for (Exception failure : sendException.getFailedMessages().values()) {
                int code = smtpReturnCode(failure);
                String message = failure.getMessage() != null ? failure.getMessage() : "";

                // recipient or message specific 5xx (unknown account, invalid address, message too large)
                if (message.contains("5.1.1") || code == 553 || message.contains("5.3.4")) {
                    return false;
                }
            }
        }

        // authentication, connection errors, timeouts, 4xx, policy blocks (5.7.1): server problems
        return true;
    }

    @Bean
    public CircuitBreaker emailCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(50)
                .minimumNumberOfCalls(20)
                .failureRateThreshold(70)
                .waitDurationInOpenState(Duration.ofMinutes(20))
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .permittedNumberOfCallsInHalfOpenState(5)
                .recordException(ResilienceConfig::isRecordableEmailException)
                .build();

        return CircuitBreaker.of("email", config);
    }

}
