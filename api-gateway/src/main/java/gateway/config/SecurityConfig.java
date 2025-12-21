package gateway.config;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Lane B: API Key authentication for submission traffic.
     * Matches /api/submit/** - API key validation happens in ApiKeyAuthFilter.
     */
    @Bean
    @Order(1)
    public SecurityWebFilterChain apiKeySecurityChain(ServerHttpSecurity http) {
        return http
            .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/submit/**"))
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
            .build();
    }

    /**
     * Lane A: JWT Resource Server for configuration traffic.
     * Frontend (React/Vue) handles Keycloak login and sends Bearer token.
     */
    @Bean
    @Order(2)
    public SecurityWebFilterChain jwtSecurityChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/api/keys/**").authenticated()
                .pathMatchers("/api/schemas/**").authenticated()
                .pathMatchers("/api/campaigns/**").authenticated()
                .pathMatchers("/api/rewards/**").authenticated()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
            .build();
    }
}
