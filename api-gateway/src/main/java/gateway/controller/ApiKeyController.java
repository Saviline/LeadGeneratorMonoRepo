package gateway.controller;

import gateway.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controller for API key management.
 * Requires JWT authentication (Lane A).
 *
 * Endpoints:
 *   POST /api/keys      - Generate new API key
 *   DELETE /api/keys    - Revoke API key
 */
@Slf4j
@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    /**
     * Generate a new API key for the authenticated customer.
     * The key is only shown once - customer must save it.
     */
    @PostMapping
    public Mono<Map<String, String>> generateKey(@AuthenticationPrincipal Jwt jwt) {
        String customerId = jwt.getSubject();
        log.info("Generating API key for customer: {}", customerId);

        return apiKeyService.generateApiKey(customerId)
            .map(key -> Map.of(
                "apiKey", key,
                "message", "Save this key - it won't be shown again"
            ));
    }

    /**
     * Revoke an API key.
     */
    @DeleteMapping
    public Mono<Map<String, Object>> revokeKey(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal Jwt jwt) {

        String apiKey = request.get("apiKey");
        String customerId = jwt.getSubject();

        log.info("Revoking API key for customer: {}", customerId);

        return apiKeyService.revokeApiKey(apiKey)
            .map(revoked -> Map.of(
                "revoked", revoked,
                "message", revoked ? "API key revoked" : "API key not found"
            ));
    }
}
