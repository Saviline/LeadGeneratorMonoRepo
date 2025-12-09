package formschema.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import formschema.core.services.FormSchemaService;
import formschema.core.services.SchemaTranslatorService;
import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import formschema.detail.mongodb.MongoDBFormSchemaRepository;
import formschema.detail.mongodb.MongoDBFormSchemaDataSource;

@Configuration
@EnableMongoRepositories(basePackages = "formschema.detail.mongodb")
public class ApplicationConfig {

    // STEP 1: Create the Repository Bean
    // Spring automatically provides the 'dataSource' because of @EnableMongoRepositories
    @Bean
    public IFormSchemaRepository<FormSchema, String> formSchemaRepository(MongoDBFormSchemaDataSource dataSource) {
        return new MongoDBFormSchemaRepository(dataSource);
    }

    // STEP 2: Create the Service Bean
    // Spring injects the 'repository' bean created in Step 1
    @Bean
    public FormSchemaService formSchemaService(IFormSchemaRepository<FormSchema, String> repository) {
        return new FormSchemaService(repository);
    }

     @Bean
    public SchemaTranslatorService schemaTranslatorService() {
        return new SchemaTranslatorService();
    }
}