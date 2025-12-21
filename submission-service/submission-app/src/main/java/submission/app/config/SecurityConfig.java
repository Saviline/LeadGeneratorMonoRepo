package submission.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Submission Service.
 *
 * This service is NOT directly exposed to the internet.
 * All requests come through the API Gateway which handles:
 * - API Key validation (Lane B traffic)
 * - X-Customer-ID header injection
 *
 * The service trusts requests with valid X-Customer-ID headers.
 * In production, network policies should restrict access to Gateway only.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/submissions/**").permitAll()
                .anyRequest().denyAll()
            )
            .build();
    }
}
