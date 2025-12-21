package gateway.filter;

import gateway.service.ApiKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Gateway filter that validates API keys for Lane B (submission traffic).
 *
 * Flow:
 * 1. Extract X-Api-Key header from request
 * 2. Look up key in Redis to get customerId
 * 3. If valid, inject X-Customer-ID header and forward
 * 4. If invalid, return 401 Unauthorized
 */
@Slf4j
@Component
public class ApiKeyAuthFilter extends AbstractGatewayFilterFactory<ApiKeyAuthFilter.Config> {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthFilter(ApiKeyService apiKeyService) {
        super(Config.class);
        this.apiKeyService = apiKeyService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-Api-Key");

            if (apiKey == null || apiKey.isBlank()) {
                log.warn("Missing X-Api-Key header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return apiKeyService.validateAndGetCustomerId(apiKey)
                .flatMap(customerId -> {
                    log.debug("API key validated. customerId={}", customerId);

                    // Inject X-Customer-ID header for downstream service
                    var mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Customer-ID", customerId)
                        .build();

                    var mutatedExchange = exchange.mutate()
                        .request(mutatedRequest)
                        .build();

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Invalid API key");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
