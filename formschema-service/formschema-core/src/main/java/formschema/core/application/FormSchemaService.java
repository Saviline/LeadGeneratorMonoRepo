package formschema.core.application;

import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;
import formschema.core.ports.outbound.IPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FormSchemaService {
    private final IFormSchemaRepository<FormSchema, String> repository;
    private final IPublisher publisher;

    public String createSchema(FormSchema schema) {

        //Save schema to persistent database
        String schemaId = repository.save(schema);
        log.info("FormSchema has been created: schema.name={}, schema.id={}", schema.getName(), schemaId);

        //Publish Schema to queues needing it. 
        publisher.PublishSchema(schema);

        //Return SchemaID
        return schemaId;
    }

    public FormSchema getSchemaById(String id) {
        return repository.findFormSchemaById(id);
    }

    public Boolean deleteSchemaById(String id) {
        return repository.deleteFormSchema(id);
    }
}