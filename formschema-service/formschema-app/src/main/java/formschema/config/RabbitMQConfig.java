package formschema.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final RabbitMQProperties properties;

    public RabbitMQConfig(RabbitMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TopicExchange Exchange() {
        return ExchangeBuilder.topicExchange(properties.getExchange()).durable(true).build();
    }

      @Bean
    public Queue validationQueue() {
        return QueueBuilder
            .durable(properties.getValidation().getQueue())
            .build();
    }

    @Bean
    public Queue businessQueue() {
        return QueueBuilder
            .durable(properties.getBusiness().getQueue())
            .build();
    }

    @Bean
    public Binding validationBinding(Queue validationQueue, TopicExchange leadgenExchange) {
        return BindingBuilder
            .bind(validationQueue)
            .to(leadgenExchange)
            .with(properties.getValidation().getRoutingKey());
    }

    @Bean
    public Binding businessBinding(Queue businessQueue, TopicExchange leadgenExchange) {
        return BindingBuilder
            .bind(businessQueue)
            .to(leadgenExchange)
            .with(properties.getBusiness().getRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // These are auto configured by Spring Boot
    //CachingConnectionFactory 
    //RabbitAdmin
    //RabbitTemplate
}
