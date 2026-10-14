package com.cristian.expensetracker.registration.tests;

import com.cristian.expensetracker.registration.RegisterRequestDTO;
import com.cristian.expensetracker.registration.RegistrationLoginAndAuthController;
import com.cristian.expensetracker.registration.RegistrationService;
import com.cristian.expensetracker.security.SecurityConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer only: the real controller and security rules, with the service replaced by a mock.
 * No database is involved.
 *
 * The CSRF token is obtained the way the React app does it (GET /csrf, then send the cookie
 * back plus the X-XSRF-TOKEN header). Spring Security's csrf() test helper is not used on
 * purpose: it swaps the token repository inside the shared filter chain, which then stops
 * writing the XSRF-TOKEN cookie for later tests in the same context.
 */
@WebMvcTest(RegistrationLoginAndAuthController.class)
@Import(SecurityConfig.class)
class RegistrationControllerSecurityTest {

    private static final String CSRF_URL = "/api/v1/auth/csrf";
    private static final String REGISTER_URL = "/api/v1/auth/register";
    private static final String CSRF_COOKIE = "XSRF-TOKEN";
    private static final String CSRF_HEADER = "X-XSRF-TOKEN";

    private static final String VALID_BODY = """
            {
              "displayName": "Cristian",
              "email": "cristian@example.com",
              "password": "ExamplePassword1",
              "timezone": "Europe/Bucharest"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    void csrfEndpointIsPublicAndSetsTokenCookie() throws Exception {
        mockMvc.perform(get(CSRF_URL))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists(CSRF_COOKIE));
    }

    @Test
    void registerWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());

        verify(registrationService, never()).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void registerWithCsrfTokenIsAcceptedWithoutLogin() throws Exception {
        Cookie csrfCookie = fetchCsrfCookie();

        mockMvc.perform(post(REGISTER_URL)
                        .cookie(csrfCookie)
                        .header(CSRF_HEADER, csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isAccepted());

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void invalidRequestReturns400WithFieldErrorAndSkipsService() throws Exception {
        Cookie csrfCookie = fetchCsrfCookie();
        String invalidEmailBody = VALID_BODY.replace("cristian@example.com", "not-an-email");

        mockMvc.perform(post(REGISTER_URL)
                        .cookie(csrfCookie)
                        .header(CSRF_HEADER, csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());

        verify(registrationService, never()).registerUser(any(RegisterRequestDTO.class));
    }

    private Cookie fetchCsrfCookie() throws Exception {
        return mockMvc.perform(get(CSRF_URL))
                .andReturn()
                .getResponse()
                .getCookie(CSRF_COOKIE);
    }
}
