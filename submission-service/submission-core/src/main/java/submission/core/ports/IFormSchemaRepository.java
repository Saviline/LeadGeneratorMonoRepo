package submission.core.ports;

import java.util.Optional;

public interface IFormSchemaRepository {
    void save(String customerId, String schemaId, String validationSchema);
    Optional<String> findByCustomerIdAndSchemaId(String customerId, String schemaId);
}
