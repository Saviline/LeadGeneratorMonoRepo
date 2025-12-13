package formschema.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private String exchange;
    
    private ValidationQueue validation = new ValidationQueue();
    private BusinessQueue business = new BusinessQueue();

    @Data
    public static class ValidationQueue {
        private String queue;
        private String routingKey;
    }

    @Data
    public static class BusinessQueue {
        private String queue;
        private String routingKey;
    }
}