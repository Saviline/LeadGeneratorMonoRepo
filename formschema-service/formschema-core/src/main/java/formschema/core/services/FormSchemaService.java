package formschema.core.services;

import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormSchemaService {
    private final IFormSchemaRepository<FormSchema, String> repository;

    public String createSchema(FormSchema schema) {
        return repository.save(schema);
    }

    public FormSchema getSchemaById(String id) {
        return repository.findFormSchemaById(id);
    }

    public Boolean deleteSchemaById(String id) {
        return repository.deleteFormSchema(id);
    }
}