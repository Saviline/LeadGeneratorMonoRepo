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

    public String createSchema(FormSchema schema, String customerId) {
        log.info("Creating schema: name={}, customerId={}", schema.getName(), customerId);

        // Set customerId from authenticated user (not from request body)
        schema.setCustomerId(customerId);

        // Save schema to persistent database
        String schemaId = repository.save(schema);
        log.info("FormSchema saved to database: schema.name={}, schema.id={}, customer.id={}",
            schema.getName(), schemaId, customerId);

        // Publish Schema to queues needing it
        publisher.PublishSchema(schema);
        log.info("FormSchema is published: schema.name={}, schema.id={}", schema.getName(), schemaId);

        log.info("Schema created successfully: id={}", schemaId);
        return schemaId;
    }

    public FormSchema getSchemaById(String id, String customerId) {
        log.debug("Getting schema: id={}, customerId={}", id, customerId);
        return repository.findByIdAndCustomerId(id, customerId)
            .orElseThrow(() -> new SchemaNotFoundException(id));
    }

    public Boolean deleteSchemaById(String id, String customerId) {
        log.info("Deleting schema: id={}, customerId={}", id, customerId);
        return repository.deleteByIdAndCustomerId(id, customerId);
    }
}