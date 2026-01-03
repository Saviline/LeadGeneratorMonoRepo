package campaign.config;

import campaign.core.application.CampaignService;
import campaign.core.ports.outbound.ICampaignRepository;
import campaign.core.ports.outbound.IFormSchemaRepository;
import campaign.core.ports.outbound.IPublisher;
import campaign.detail.messaging.CampaignProducer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class ApplicationConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public ApplicationConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public IPublisher campaignProducer(RabbitTemplate rabbitTemplate) {
        return new CampaignProducer(
                rabbitTemplate,
                rabbitMQProperties.getExchange(),
                rabbitMQProperties.getRoutingKey());
    }

    @Bean
    public CampaignService campaignService(ICampaignRepository campaignRepository,
                                            IFormSchemaRepository formSchemaValidator,
                                            IPublisher publisher) {
        return new CampaignService(campaignRepository, formSchemaValidator, publisher);
    }
}
