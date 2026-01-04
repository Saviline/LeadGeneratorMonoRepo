package campaign.detail.http;

import campaign.core.ports.outbound.IFormSchemaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class FormSchemaHttpClient implements IFormSchemaRepository {

    public static final String JWT_CONTEXT_KEY = "jwt-token";

    private final WebClient webClient;

    public FormSchemaHttpClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Boolean> schemaExists(String schemaId, String customerId) {
        log.debug("Checking schema exists: schemaId={}, customerId={}", schemaId, customerId);

        return Mono.deferContextual(ctx -> {
            WebClient.RequestHeadersSpec<?> request = webClient.get()
                    .uri("/api/schemas/{schemaId}", schemaId);

            if (ctx.hasKey(JWT_CONTEXT_KEY)) {
                String token = ctx.get(JWT_CONTEXT_KEY);
                request = webClient.get()
                        .uri("/api/schemas/{schemaId}", schemaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                log.debug("Forwarding JWT token to FormSchema service");
            } else {
                log.warn("No JWT token found in context for FormSchema service call");
            }

            return request.exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    log.debug("Schema found: schemaId={}", schemaId);
                    return Mono.just(true);
                } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    log.debug("Schema not found: schemaId={}", schemaId);
                    return Mono.just(false);
                } else {
                    log.error("Unexpected response from FormSchema service: status={}", response.statusCode());
                    return Mono.just(false);
                }
            });
        })
        .onErrorResume(e -> {
            log.error("Error checking schema existence: schemaId={}, error={}", schemaId, e.getMessage());
            return Mono.just(false);
        });
    }
}
