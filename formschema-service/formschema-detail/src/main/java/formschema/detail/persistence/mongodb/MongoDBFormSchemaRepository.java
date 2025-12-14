package formschema.detail.persistence.mongodb;

import org.springframework.data.mongodb.core.MongoTemplate;

import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MongoDBFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

    private final MongoTemplate mongoTemplate;

    @Override
    public String save(FormSchema entity){
        String id = mongoTemplate.save(entity).getId();
        log.debug("Schema has been saved in mongoDB database: schema.id={}", id);
        return id;
    }

    @Override
    public FormSchema findFormSchemaById(String id) {
        return mongoTemplate.findById(id, FormSchema.class);
    }

    @Override
    public Boolean deleteFormSchema(String id) {

        throw new UnsupportedOperationException("Unimplemented method 'deleteFormSchema'");
    }

}