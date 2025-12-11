package formschema.app.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import formschema.core.services.FormSchemaService;
import formschema.core.services.SchemaTranslatorService;
import formschema.core.interfaces.IFormSchemaRepository;
import formschema.core.models.FormSchema;

import org.springframework.data.mongodb.core.MongoTemplate;
import formschema.detail.mongodb.MongoDBFormSchemaRepository;
import formschema.detail.rabbitmq.FormSchemaProducer;

@Configuration
public class ApplicationConfig {

    // STEP 1: Create the Repository Bean
    // Spring automatically provides the 'dataSource' because of @EnableMongoRepositories
    @Bean
    public IFormSchemaRepository<FormSchema, String> formSchemaRepository(MongoTemplate mongoTemplate) {
        return new MongoDBFormSchemaRepository(mongoTemplate);
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

     @Bean
    public FormSchemaProducer formSchemaProducer(RabbitTemplate rabbitTemplate) {
        return new FormSchemaProducer(rabbitTemplate);
    }
}