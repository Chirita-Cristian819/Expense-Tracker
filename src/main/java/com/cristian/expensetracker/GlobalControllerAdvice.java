package com.cristian.expensetracker;

import com.cristian.expensetracker.exceptions.EmailNotVerifiedException;
import com.cristian.expensetracker.exceptions.ExpenseCategoryNotFoundException;
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
    public ResponseEntity<String> handleExpenseCategoryNotFoundException(
            ExpenseCategoryNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<String> handleDatabaseUnavailable(
            DataAccessResourceFailureException exception) {
        log.error("Database resource access failed; exception type: {}",
                exception.getClass().getName());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable");
    }

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<String> handlePersistenceFailure(RuntimeException exception) {
        log.error("Persistence operation failed; exception type: {}",
                exception.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleInvalidEmailOrPasswordException(
            AuthenticationException exception
    ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<String> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException exception){
        log.error("Authentication service failed during login", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service Unavailable");
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<String> handleEmailNotVerifiedException(){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email not verified");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException(Exception exception) {
        log.error("Unexpected request failure; exception type: {}",
                exception.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred");
    }

}
