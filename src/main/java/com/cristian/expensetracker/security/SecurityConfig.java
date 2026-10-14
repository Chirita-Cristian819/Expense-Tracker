package com.cristian.expensetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);

                return new ProviderManager(provider);
        }

        @Bean
        public SecurityContextRepository securityContextRepository() {
                return new HttpSessionSecurityContextRepository();
        }

        private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/currencies",
            "/api/v1/expense-categories",
            "/api/v1/expense-categories/*/expense-types",
            "/api/v1/payment-methods",
            "/api/v1/auth/csrf"
        };

        private static final String[] PUBLIC_POST_ENDPOINTS = {
                "/api/v1/auth/register",
                "/api/v1/auth/login"
        };

        // Swagger UI + generated OpenAPI description (springdoc).
        private static final String[] API_DOCS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                       SecurityContextRepository securityContextRepository) throws Exception {
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.GET, API_DOCS).permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                        ).csrf(CsrfConfigurer::spa)
                        .securityContext(context -> context
                                .securityContextRepository(securityContextRepository))
                        .logout(logout -> logout
                                .logoutUrl("/api/v1/auth/logout")
                                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                                .deleteCookies("JSESSIONID"))
                        .formLogin(Customizer.withDefaults());

            return http.build();
        }
}

