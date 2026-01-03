package submission.app.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import submission.core.application.SubmissionService;
import submission.core.domain.Submission;
import submission.core.domain.Campaign;
import submission.core.ports.ICacheFormSchema;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositoryCampaign;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;
import submission.detail.messaging.FormSchemaListener;
import submission.detail.messaging.CampaignListener;
import submission.detail.messaging.SubmissionProducer;
import submission.detail.RedisFormSchemaCache;
import submission.detail.JsonSchemaValidator;

import java.util.Optional;

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
    public ICacheFormSchema redisFormSchemaCache(RedisTemplate<String, String> redisTemplate) {
        return new RedisFormSchemaCache(redisTemplate);
    }

    @Bean
    public FormSchemaListener formSchemaListener(ICacheFormSchema cache, ObjectMapper objectMapper) {
        return new FormSchemaListener(cache, objectMapper);
    }

    @Bean
    public IRepositorySubmission submissionRepository() {
        return new IRepositorySubmission() {
            @Override
            public void save(Submission submission) {
            }

            @Override
            public Submission getById(String id) {
                return null;
            }
        };
    }

    @Bean
    public IRepositoryCampaign campaignRepository() {
        return new IRepositoryCampaign() {
            @Override
            public void save(Campaign campaign) {
            }

            @Override
            public Optional<Campaign> findByIdAndCustomerId(String campaignId, String customerId) {
                return Optional.empty();
            }

            @Override
            public void delete(String campaignId) {
            }
        };
    }

    @Bean
    public CampaignListener campaignListener(IRepositoryCampaign campaignRepository) {
        return new CampaignListener(campaignRepository);
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
            ICacheFormSchema cacheFormSchema,
            IPublish<Submission> publisher,
            IValidate validator,
            IRepositoryCampaign campaignRepository) {
        return new SubmissionService(submissionRepository, cacheFormSchema, publisher, validator, campaignRepository);
    }
}
