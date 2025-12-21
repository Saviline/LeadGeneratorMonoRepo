package gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for API key management.
 *
 * Redis structure:
 *   Key:   apikey:{hashed_key}
 *   Value: customerId
 *
 * The actual API key is hashed before storage for security.
 * Customer sees: pk_live_abc123xyz...
 * Redis stores: apikey:{sha256_of_key} -> customerId
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "apikey:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Validates an API key and returns the associated customerId.
     * Returns empty Mono if key is invalid.
     */
    public Mono<String> validateAndGetCustomerId(String apiKey) {
        String hashedKey = hashKey(apiKey);
        String redisKey = KEY_PREFIX + hashedKey;

        return redisTemplate.opsForValue().get(redisKey)
            .doOnNext(customerId -> log.debug("API key found for customer: {}", customerId))
            .doOnSubscribe(s -> log.debug("Validating API key: {}", redisKey));
    }

    /**
     * Generates a new API key for a customer.
     * Returns the plain-text key (only shown once to user).
     */
    public Mono<String> generateApiKey(String customerId) {
        String plainKey = generateSecureKey();
        String hashedKey = hashKey(plainKey);
        String redisKey = KEY_PREFIX + hashedKey;

        return redisTemplate.opsForValue().set(redisKey, customerId)
            .thenReturn(plainKey)
            .doOnSuccess(key -> log.info("API key generated for customer: {}", customerId));
    }

    /**
     * Revokes an API key.
     */
    public Mono<Boolean> revokeApiKey(String apiKey) {
        String hashedKey = hashKey(apiKey);
        String redisKey = KEY_PREFIX + hashedKey;

        return redisTemplate.delete(redisKey)
            .map(count -> count > 0)
            .doOnSuccess(revoked -> log.info("API key revoked: {}", revoked));
    }

    /**
     * Generates a secure random API key.
     * Format: pk_live_{32_random_chars}
     */
    private String generateSecureKey() {
        byte[] bytes = new byte[24];
        SECURE_RANDOM.nextBytes(bytes);
        String random = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return "pk_live_" + random;
    }

    /**
     * Hashes the API key for storage.
     * Using simple SHA-256 for now.
     */
    private String hashKey(String apiKey) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(apiKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash API key", e);
        }
    }
}
