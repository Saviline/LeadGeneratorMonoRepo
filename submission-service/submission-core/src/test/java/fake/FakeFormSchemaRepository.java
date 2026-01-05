package fake;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import submission.core.ports.IFormSchemaRepository;

public class FakeFormSchemaRepository implements IFormSchemaRepository {

    ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public void save(String customerId, String schemaId, String validationSchema) {
        cache.put(buildKey(customerId, schemaId), validationSchema);
    }

    @Override
    public Optional<String> findByCustomerIdAndSchemaId(String customerId, String schemaId) {
        return Optional.ofNullable(cache.get(buildKey(customerId, schemaId)));
    }

    private String buildKey(String customerId, String schemaId) {
        return customerId + ":" + schemaId;
    }
}
