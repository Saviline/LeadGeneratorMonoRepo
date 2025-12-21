package formschema.core.ports.outbound;

import java.util.Optional;

public interface IFormSchemaRepository<T, ID> {

    ID save(T entity);

    Optional<T> findByIdAndCustomerId(ID id, String customerId);

    Boolean deleteByIdAndCustomerId(ID id, String customerId);

}

