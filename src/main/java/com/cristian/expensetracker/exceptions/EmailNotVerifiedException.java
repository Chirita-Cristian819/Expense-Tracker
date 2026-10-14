package com.cristian.expensetracker.exceptions;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException(Long userId) {
        super("Email not verified for user %d".formatted(userId));
    }
}
