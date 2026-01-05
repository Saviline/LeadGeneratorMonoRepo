package campaign.config;

import campaign.core.application.CampaignService;
import campaign.core.ports.outbound.ICampaignRepository;
import campaign.core.ports.outbound.ICustomerServiceClient;
import campaign.core.ports.outbound.IFormSchemaRepository;
import campaign.core.ports.outbound.IPublisher;
import campaign.detail.http.FormSchemaHttpClient;
import campaign.detail.http.MockCustomerServiceClient;
import campaign.detail.messaging.CampaignProducer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class ApplicationConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public ApplicationConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public WebClient formSchemaWebClient(@Value("${services.formschema.url}") String formSchemaUrl) {
        return WebClient.builder()
                .baseUrl(formSchemaUrl)
                .build();
    }

    @Bean
    public IFormSchemaRepository formSchemaRepository(WebClient formSchemaWebClient) {
        return new FormSchemaHttpClient(formSchemaWebClient);
    }

    @Bean
    public ICustomerServiceClient customerServiceClient() {
        return new MockCustomerServiceClient();
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
                                           IFormSchemaRepository formSchemaRepository,
                                           ICustomerServiceClient customerServiceClient,
                                           IPublisher publisher) {
        return new CampaignService(campaignRepository, formSchemaRepository, customerServiceClient, publisher);
    }
}
