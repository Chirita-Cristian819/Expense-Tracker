package com.cristian.expensetracker.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(@NotBlank
                              @Size(max = 320)
                              @Email(message = "Email format is invalid or empty")
                              String email,

                              @NotBlank
                              @Size(max = 200)
                              String password) {
}
