package formschema.detail.mongodb;

import org.springframework.data.mongodb.core.MongoTemplate;

import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoDBFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

    private final MongoTemplate mongoTemplate;

    @Override
    public String save(FormSchema entity){
        return mongoTemplate.save(entity).getId();
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