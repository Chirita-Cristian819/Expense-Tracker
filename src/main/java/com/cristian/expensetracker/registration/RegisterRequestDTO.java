package com.cristian.expensetracker.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(title = "Register Request")
public record RegisterRequestDTO(
        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank(message = "Email is required")
        @Size(max = 320, message = "Email must have at most 320 characters")
        @Email(message = "Email format is invalid or empty")
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{10,30}$",
                message = "Password must contain 10–30 characters, "
                        + "at least one uppercase letter, one lowercase letter and one digit"
        )
        String password,

        String timezone
) {
}