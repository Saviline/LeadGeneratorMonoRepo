package campaign.detail.http;

import campaign.core.ports.outbound.ICustomerServiceClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class MockCustomerServiceClient implements ICustomerServiceClient {

    @Override
    public Mono<List<String>> getIntegrationEndpoints(String customerId) {
        log.debug("Mock: Getting integration endpoints for customerId={}", customerId);

        List<String> mockIntegrations = List.of(
                "550e8400-e29b-41d4-a716-446655440011",
                "550e8400-e29b-41d4-a716-446655440010"
        );

        return Mono.just(mockIntegrations);
    }
}
