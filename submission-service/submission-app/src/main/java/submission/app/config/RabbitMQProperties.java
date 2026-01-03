package submission.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private String exchange;
    private String routingKey;

    private QueueConfig formSchemaQueue = new QueueConfig();
    private QueueConfig campaignQueue = new QueueConfig();

    @Data
    public static class QueueConfig {
        private String queue;
        private String routingKey;
    }
}
