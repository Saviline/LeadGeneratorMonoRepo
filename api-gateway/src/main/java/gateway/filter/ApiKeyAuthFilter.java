package gateway.filter;

import gateway.service.ApiKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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

                    var mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Customer-ID", customerId)
                        .build();

                    var mutatedExchange = exchange.mutate()
                        .request(mutatedRequest)
                        .build();

                    return chain.filter(mutatedExchange).then(Mono.just(customerId));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Invalid API key");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete().then(Mono.empty());
                }))
                .then();
        };
    }

    public static class Config {
    }
}
