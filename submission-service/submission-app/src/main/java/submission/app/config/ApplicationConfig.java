package submission.app.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import submission.core.application.SubmissionService;
import submission.core.domain.Submission;
import submission.core.ports.ICampaignSettingsRepository;
import submission.core.ports.IFormSchemaRepository;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;
import submission.detail.messaging.FormSchemaListener;
import submission.detail.messaging.CampaignListener;
import submission.detail.messaging.SubmissionProducer;
import submission.detail.mongodb.MongoDBSubmissionRepository;
import submission.detail.RedisCampaignSettingsRepository;
import submission.detail.RedisFormSchemaRepository;
import submission.detail.JsonSchemaValidator;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class ApplicationConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public ApplicationConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public IValidate validatorService(ObjectMapper objectMapper) {
        return new JsonSchemaValidator(objectMapper);
    }

    @Bean
    public IFormSchemaRepository formSchemaRepository(RedisTemplate<String, String> redisTemplate) {
        return new RedisFormSchemaRepository(redisTemplate);
    }

    @Bean
    public FormSchemaListener formSchemaListener(IFormSchemaRepository formSchemaRepository, ObjectMapper objectMapper) {
        return new FormSchemaListener(formSchemaRepository, objectMapper);
    }

    @Bean
    public IRepositorySubmission submissionRepository(MongoTemplate mongoTemplate) {
        return new MongoDBSubmissionRepository(mongoTemplate);
    }

    @Bean
    public ICampaignSettingsRepository campaignSettingsRepository(RedisTemplate<String, String> redisTemplate) {
        return new RedisCampaignSettingsRepository(redisTemplate);
    }

    @Bean
    public CampaignListener campaignListener(ICampaignSettingsRepository campaignSettingsRepository) {
        return new CampaignListener(campaignSettingsRepository);
    }

    @Bean
    public IPublish<Submission> submissionProducer(RabbitTemplate rabbitTemplate) {
        return new SubmissionProducer(
                rabbitMQProperties.getExchange(),
                rabbitMQProperties.getRoutingKey(),
                rabbitTemplate);
    }

    @Bean
    public SubmissionService submissionService(
            IRepositorySubmission submissionRepository,
            IFormSchemaRepository formSchemaRepository,
            IPublish<Submission> publisher,
            IValidate validator,
            ICampaignSettingsRepository campaignSettingsRepository) {
        return new SubmissionService(submissionRepository, formSchemaRepository, publisher, validator, campaignSettingsRepository);
    }
}
