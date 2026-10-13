package com.cristian.expensetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/currencies",
            "/api/v1/expense-categories",
            "/api/v1/expense-categories/*/expense-types",
            "/api/v1/payment-methods",
            "/error"
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
                            .requestMatchers(HttpMethod.GET, API_DOCS).permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(Customizer.withDefaults());

            return http.build();
        }
}

