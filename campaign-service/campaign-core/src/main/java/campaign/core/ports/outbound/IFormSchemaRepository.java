package campaign.core.ports.outbound;

import reactor.core.publisher.Mono;

public interface IFormSchemaRepository {

    Mono<Boolean> schemaExists(String schemaId, String customerId);

}
