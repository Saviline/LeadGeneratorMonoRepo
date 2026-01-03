package formschema.core.ports.outbound;

import java.util.List;
import java.util.Optional;

import formschema.core.domain.FormSchema;

public interface IFormSchemaRepository<T, ID> {

    ID save(T entity);

    Optional<T> findByIdAndCustomerId(ID id, String customerId);

    Boolean deleteByIdAndCustomerId(ID id, String customerId);

    Optional<List<FormSchema>> getAllByCustomerId(String customerId);

}

