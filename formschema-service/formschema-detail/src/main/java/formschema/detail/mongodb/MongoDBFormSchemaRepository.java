package formschema.detail.mongodb;

import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoDBFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

    final MongoDBFormSchemaDataSource mongoDBFormSchemaDataSource;  

    @Override
    public String save(FormSchema entity){
        return mongoDBFormSchemaDataSource.save(entity).getId();
    }

    @Override
    public FormSchema findFormSchemaById(String id) {
        return mongoDBFormSchemaDataSource.findById(id).orElse(null);
    }

    @Override
    public Boolean deleteFormSchema(String id) {

        throw new UnsupportedOperationException("Unimplemented method 'deleteFormSchema'");
    }

}