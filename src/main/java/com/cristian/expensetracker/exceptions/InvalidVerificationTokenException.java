package com.cristian.expensetracker.exceptions;

import com.cristian.expensetracker.tokens.TokenPurpose;
import lombok.Getter;

@Getter
public class InvalidVerificationTokenException extends RuntimeException {

    private final TokenPurpose purpose;

    public InvalidVerificationTokenException(TokenPurpose purpose, String message) {
        super(message);
        this.purpose = purpose;
    }
}