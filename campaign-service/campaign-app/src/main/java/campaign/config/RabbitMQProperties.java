package campaign.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private String exchange;
    private String routingKey;

    private Queue formSchemaQueue = new Queue();

    @Data
    public static class Queue {
        private String queue;
        private String routingKey;
    }
}
