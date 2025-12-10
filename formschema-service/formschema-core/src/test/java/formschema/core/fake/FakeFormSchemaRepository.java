package formschema.core.fake;

import java.util.Map;

import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;

public class FakeFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

  Map<String, FormSchema> formSchemaStore;

   public FakeFormSchemaRepository() {
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
