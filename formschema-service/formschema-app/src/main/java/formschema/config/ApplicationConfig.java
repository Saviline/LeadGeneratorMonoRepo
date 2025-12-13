package formschema.config;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import formschema.core.application.FormSchemaService;
import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;
import formschema.core.ports.outbound.IPublisher;
import formschema.core.ports.outbound.ITranslator;

import org.springframework.data.mongodb.core.MongoTemplate;

import formschema.detail.messaging.FormSchemaProducer;
import formschema.detail.persistence.mongodb.MongoDBFormSchemaRepository;
import formschema.detail.translation.SchemaTranslator;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class ApplicationConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public ApplicationConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public IFormSchemaRepository<FormSchema, String> formSchemaRepository(MongoTemplate mongoTemplate) {
        return new MongoDBFormSchemaRepository(mongoTemplate);
    }

    @Bean
    public ITranslator<Map<String, Object>> schemaTranslator() {
        return new SchemaTranslator();
    }
    
    @Bean
    public IPublisher formSchemaProducer(RabbitTemplate rabbitTemplate, ITranslator<Map<String, Object>> schemaTranslator) {
        return new FormSchemaProducer(rabbitMQProperties.getExchange(), rabbitMQProperties.getValidation().getRoutingKey(), rabbitMQProperties.getBusiness().getRoutingKey(), schemaTranslator, rabbitTemplate);
    }

    //DOMAIN SERVICE
    @Bean
    public FormSchemaService formSchemaService(IFormSchemaRepository<FormSchema, String> repository, IPublisher publisher) {
        return new FormSchemaService(repository, publisher);
    }
}