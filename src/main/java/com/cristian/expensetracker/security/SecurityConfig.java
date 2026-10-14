package com.cristian.expensetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/currencies",
            "/api/v1/expense-categories",
            "/api/v1/expense-categories/*/expense-types",
            "/api/v1/payment-methods",
            "/api/v1/auth/csrf"
        };

        private static final String[] PUBLIC_POST_ENDPOINTS = {
                "/api/v1/auth/register"
        };

        // Swagger UI + generated OpenAPI description (springdoc).
        private static final String[] API_DOCS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                            .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                            .requestMatchers(HttpMethod.GET, API_DOCS).permitAll()
                            .requestMatchers("/error").permitAll()
                            .anyRequest().authenticated()
                    ).csrf(CsrfConfigurer::spa)
                    .formLogin(Customizer.withDefaults());

            return http.build();
        }
}

