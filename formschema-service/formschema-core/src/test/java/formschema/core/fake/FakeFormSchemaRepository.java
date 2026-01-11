package formschema.core.fake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;

public class FakeFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

    Map<String, FormSchema> formSchemaStore;

    public FakeFormSchemaRepository() {
        this.formSchemaStore = new HashMap<>();
    }

    @Override
    public String save(FormSchema entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        formSchemaStore.put(entity.getId(), entity);
        return entity.getId();
    }

    @Override
    public Optional<FormSchema> findByIdAndCustomerId(String id, String customerId) {
        FormSchema schema = formSchemaStore.get(id);
        if (schema != null && schema.getCustomerId() != null && schema.getCustomerId().equals(customerId)) {
            return Optional.of(schema);
        }
        return Optional.empty();
    }

    @Override
    public Boolean deleteByIdAndCustomerId(String id, String customerId) {
        FormSchema schema = formSchemaStore.get(id);
        if (schema != null && schema.getCustomerId() != null && schema.getCustomerId().equals(customerId)) {
            formSchemaStore.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<List<FormSchema>> getAllByCustomerId(String customerId) {
        List<FormSchema> result = formSchemaStore.values().stream()
                .filter(schema -> schema.getCustomerId() != null && schema.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        return Optional.of(result);
    }
}
