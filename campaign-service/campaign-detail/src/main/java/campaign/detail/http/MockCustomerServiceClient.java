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
                "integration-001",
                "integration-002",
                "integration-003"
        );

        return Mono.just(mockIntegrations);
    }
}
