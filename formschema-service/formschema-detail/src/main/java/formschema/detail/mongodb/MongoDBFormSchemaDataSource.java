package formschema.detail.mongodb;
import org.springframework.data.mongodb.repository.MongoRepository;
import formschema.core.models.FormSchema;

public interface MongoDBFormSchemaDataSource extends MongoRepository<FormSchema, String> {} 

