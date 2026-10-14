package com.cristian.expensetracker.registration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationLoginAndAuthController {

    private final RegistrationService registrationService;

    public RegistrationLoginAndAuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "202", description = "Operation accepted")
    @ApiResponse(responseCode = "400", description = "Invalid registration request")
    public ResponseEntity<Void> registerUser(
            @Valid @RequestBody RegisterRequestDTO registrationRequest){
        registrationService.registerUser(registrationRequest);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/csrf")
    @Operation(summary = "Return CSRF token")
    public  ResponseEntity<Void> csrfToken(CsrfToken token){
        token.getToken();
        return ResponseEntity.noContent().build();
    }


}
