package com.cristian.expensetracker.apiresponses;

/*
* ErrorCode enum used to pass enumerated errors in the ErrorResponse
* for easier React front-end error handling
*/
public enum ErrorCode {
    RESOURCE_NOT_FOUND,
    INVALID_CREDENTIALS,
    EMAIL_NOT_VERIFIED,
    INVALID_VERIFICATION_LINK,
    INVALID_PASSWORD_RESET_LINK,
    SERVICE_UNAVAILABLE,
    INTERNAL_ERROR
}
