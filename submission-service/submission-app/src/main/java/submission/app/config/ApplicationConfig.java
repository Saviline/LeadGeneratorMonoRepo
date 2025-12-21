package submission.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import submission.core.application.SubmissionService;
import submission.core.domain.Submission;
import submission.core.ports.ICacheFormSchema;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositoryCampaign;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;
import submission.detail.FormSchemaListener;
import submission.detail.RedisFormSchemaCache;
import submission.detail.JsonSchemaValidator;

import java.util.Optional;
import submission.core.domain.Campaign;

@Configuration
public class ApplicationConfig {

    @Bean
    public IValidate validatorService() {
        return new JsonSchemaValidator(new ObjectMapper());
    }

    @Bean
    public ICacheFormSchema redisFormSchemaCache(RedisTemplate<String, String> redisTemplate) {
        return new RedisFormSchemaCache(redisTemplate);
    }

    @Bean
    public FormSchemaListener formSchemaListener(RedisFormSchemaCache cache) {
        return new FormSchemaListener(cache);
    }

    @Bean
    public IRepositorySubmission submissionRepository() {
        // TODO: Replace with actual MongoDB implementation
        return new IRepositorySubmission() {
            @Override
            public void save(Submission submission) {
                // Stub implementation
            }

            @Override
            public Submission getById(String id) {
                return null;
            }
        };
    }

    @Bean
    public IRepositoryCampaign campaignRepository() {
        // TODO: Replace with actual implementation
        return new IRepositoryCampaign() {
            @Override
            public void save(Campaign campaign) {
                // Stub implementation
            }

            @Override
            public Optional<Campaign> findByIdAndCustomerId(String campaignId, String customerId) {
                return Optional.empty();
            }

            @Override
            public void delete(String campaignId) {
                // Stub implementation
            }
        };
    }

    @Bean
    public IPublish<Submission> submissionPublisher() {
        // TODO: Replace with actual Kafka/RabbitMQ implementation
        return new IPublish<Submission>() {
            @Override
            public void publish(Submission entity) {
                // Stub implementation
            }
        };
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
