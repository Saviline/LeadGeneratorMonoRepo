package formschema.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import formschema.core.application.FormSchemaService;
import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;

import org.springframework.data.mongodb.core.MongoTemplate;

import formschema.detail.messaging.FormSchemaProducer;
import formschema.detail.persistence.mongodb.MongoDBFormSchemaRepository;
import formschema.detail.translation.SchemaTranslator;

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
    public SchemaTranslator schemaTranslator() {
        return new SchemaTranslator();
    }

     @Bean
    public FormSchemaProducer formSchemaProducer(RabbitTemplate rabbitTemplate) {
        return new FormSchemaProducer(rabbitTemplate);
    }
}