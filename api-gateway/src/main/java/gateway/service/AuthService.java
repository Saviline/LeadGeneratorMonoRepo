package gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class AuthService {

    private final WebClient webClient;
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;

    public AuthService(
            WebClient.Builder webClientBuilder,
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret) {

        this.webClient = webClientBuilder.build();
        this.tokenEndpoint = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        log.info("AuthService initialized with token endpoint: {}", tokenEndpoint);
    }

    public Mono<Map<String, Object>> login(String username, String password) {
        log.debug("Attempting login for user: {}", username);

        return webClient.post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters
                .fromFormData("grant_type", "password")
                .with("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("username", username)
                .with("password", password)
                .with("scope", "openid profile email"))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(Map.class)
                        .map(body -> {
                            log.info("Login successful for user: {}", username);
                            return Map.<String, Object>of(
                                "accessToken", body.get("access_token"),
                                "refreshToken", body.get("refresh_token"),
                                "expiresIn", body.get("expires_in"),
                                "tokenType", body.get("token_type")
                            );
                        });
                } else {
                    return response.bodyToMono(Map.class)
                        .flatMap(errorBody -> {
                            log.error("Keycloak error response: {}", errorBody);
                            String errorDesc = (String) errorBody.getOrDefault("error_description",
                                (String) errorBody.getOrDefault("error", "Unknown error"));
                            return Mono.error(new RuntimeException(errorDesc));
                        });
                }
            });
    }

    public Mono<Map<String, Object>> refresh(String refreshToken) {
        log.debug("Attempting token refresh");

        return webClient.post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters
                .fromFormData("grant_type", "refresh_token")
                .with("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("refresh_token", refreshToken))
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                log.debug("Token refresh successful");
                return Map.of(
                    "accessToken", response.get("access_token"),
                    "refreshToken", response.get("refresh_token"),
                    "expiresIn", response.get("expires_in"),
                    "tokenType", response.get("token_type")
                );
            })
            .doOnError(e -> log.warn("Token refresh failed: {}", e.getMessage()));
    }

    public Mono<Void> logout(String refreshToken) {
        log.debug("Attempting logout");

        String logoutEndpoint = tokenEndpoint.replace("/token", "/logout");

        return webClient.post()
            .uri(logoutEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters
                .fromFormData("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("refresh_token", refreshToken))
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("Logout successful"))
            .doOnError(e -> log.warn("Logout failed: {}", e.getMessage()));
    }
}
