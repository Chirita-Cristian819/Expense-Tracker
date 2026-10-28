package com.cristian.expensetracker;

import com.cristian.expensetracker.apiresponses.ErrorCode;
import com.cristian.expensetracker.apiresponses.ErrorResponse;
import com.cristian.expensetracker.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(ExpenseCategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExpenseCategoryNotFoundException(
            ExpenseCategoryNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseUnavailable(
            DataAccessResourceFailureException exception) {
        log.error("Database resource access failed; exception type: {}",
                exception.getClass().getName());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(ErrorCode.SERVICE_UNAVAILABLE, "Service temporarily unavailable"));
    }

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ErrorResponse> handlePersistenceFailure(RuntimeException exception) {
        log.error("Persistence operation failed; exception type: {}",
                exception.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.INTERNAL_ERROR, "An internal error occurred"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmailOrPasswordException(
            AuthenticationException exception
    ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password"));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException exception){
        log.error("Authentication service failed during login", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(ErrorCode.SERVICE_UNAVAILABLE, "Service Unavailable"));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ErrorCode.EMAIL_NOT_VERIFIED, "Email not verified"));
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationTokenException(InvalidVerificationTokenException ex){
        return switch (ex.getPurpose()) {
            case EMAIL_VERIFICATION -> ResponseEntity.badRequest().
                    body(new ErrorResponse(ErrorCode.INVALID_VERIFICATION_LINK,
                            "Email verification link is expired or invalid!"));
            case PASSWORD_RESET -> ResponseEntity.badRequest().
                    body(new ErrorResponse(ErrorCode.INVALID_PASSWORD_RESET_LINK,
                            "Password reset link is expired or invalid!"));
        };
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.error("Unexpected request failure; exception type: {}",
                exception.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.INTERNAL_ERROR, "An internal error occurred"));
    }

}
