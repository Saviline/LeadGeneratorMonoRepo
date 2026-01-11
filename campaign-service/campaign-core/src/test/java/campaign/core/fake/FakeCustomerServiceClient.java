package campaign.core.fake;

import campaign.core.ports.outbound.ICustomerServiceClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeCustomerServiceClient implements ICustomerServiceClient {

    private final Map<String, List<String>> integrations = new HashMap<>();

    public void setIntegrations(String customerId, List<String> integrationIds) {
        integrations.put(customerId, integrationIds);
    }

    public void clear() {
        integrations.clear();
    }

    @Override
    public Mono<List<String>> getIntegrationEndpoints(String customerId) {
        return Mono.just(integrations.getOrDefault(customerId, List.of()));
    }
}
