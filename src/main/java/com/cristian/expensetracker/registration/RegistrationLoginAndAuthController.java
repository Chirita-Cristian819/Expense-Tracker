package com.cristian.expensetracker.registration;

import com.cristian.expensetracker.users.UserDTO;
import com.cristian.expensetracker.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationLoginAndAuthController {

    private final RegistrationService registrationService;
    private final SecurityContextRepository securityContextRepository;
    private final UserService userService;

    public RegistrationLoginAndAuthController(RegistrationService registrationService,
                                              SecurityContextRepository securityContextRepository,
                                              UserService userService) {
        this.registrationService = registrationService;
        this.securityContextRepository = securityContextRepository;
        this.userService = userService;

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

    @PostMapping("/login")
    @Operation(summary = "Login user")
    @ApiResponse(responseCode = "202", description = "Operation accepted")
    @ApiResponse(responseCode = "401", description = "Invalid username or password")
    @ApiResponse(responseCode = "403", description = "Email not verified")
    public ResponseEntity<Void> loginUser(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletRequest request,
            HttpServletResponse response){

        Authentication authentication = registrationService.loginUser(loginRequestDTO);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        /* Session fixation:
         if a session already existed before login, give it a new ID
         */
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }

        /* Store the context in the session ->
          creates the session and sends the JSESSIONID cookie
         */
        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Return the current user")
    @ApiResponse(responseCode = "200", description = "Current user data")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserDataByPrincipal(user.getUsername()));
    }

    @GetMapping("/csrf")
    @Operation(summary = "Return CSRF token")
    public  ResponseEntity<Void> csrfToken(CsrfToken token){
        token.getToken();
        return ResponseEntity.noContent().build();
    }


}
