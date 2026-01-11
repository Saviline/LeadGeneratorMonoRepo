package campaign.core.fake;

import campaign.core.ports.outbound.IFormSchemaRepository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public class FakeFormSchemaRepository implements IFormSchemaRepository {

    private final Set<String> existingSchemas = new HashSet<>();

    public void addSchema(String schemaId, String customerId) {
        existingSchemas.add(schemaId + ":" + customerId);
    }

    public void removeSchema(String schemaId, String customerId) {
        existingSchemas.remove(schemaId + ":" + customerId);
    }

    public void clear() {
        existingSchemas.clear();
    }

    @Override
    public Mono<Boolean> schemaExists(String schemaId, String customerId) {
        return Mono.just(existingSchemas.contains(schemaId + ":" + customerId));
    }
}
