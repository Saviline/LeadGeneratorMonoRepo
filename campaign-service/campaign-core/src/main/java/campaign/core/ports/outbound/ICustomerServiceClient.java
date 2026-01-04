package campaign.core.ports.outbound;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ICustomerServiceClient {

    Mono<List<String>> getIntegrationEndpoints(String customerId);

}
