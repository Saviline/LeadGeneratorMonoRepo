package formschema.detail.persistence.inmemory;
import java.util.Map;

import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;

public class InMemoryFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

    Map<String, FormSchema> formSchemaStore;

   public InMemoryFormSchemaRepository() {
        this.formSchemaStore =  new java.util.HashMap<>();
    }

    @Override
    public String save(FormSchema entity) {
        entity.setId(java.util.UUID.randomUUID().toString());
        formSchemaStore.put(entity.getId(), entity);
        return entity.getId();
    }

    @Override
    public FormSchema findFormSchemaById(String id) {
        return formSchemaStore.get(id);
    }

    @Override
    public Boolean deleteFormSchema(String id) {
        return formSchemaStore.remove(id) != null;
    }
}
