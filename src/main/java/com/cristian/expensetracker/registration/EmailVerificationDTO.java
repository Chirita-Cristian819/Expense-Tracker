package com.cristian.expensetracker.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailVerificationDTO(@NotBlank(message = "Email is required")
                                   @Size(max = 320, message = "Email must have at most 320 characters")
                                   @Email(message = "Email format is invalid or empty")
                                   String email) {
}
