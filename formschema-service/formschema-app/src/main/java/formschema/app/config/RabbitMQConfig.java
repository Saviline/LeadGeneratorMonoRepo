package formschema.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //Exhange
    @Value("${rabbitmq.exchange.name}")
    String exchange;
    //Queue
    @Value("${rabbitmq.lead.queue.name}")
    String queueLead;
    //Routing key
    @Value("${rabbitmq.lead.routingkey.name}")
    String routingKeyLead;

    @Value("${rabbitmq.submission.queue.name}")
    String queueSubmission;
    //Routing key
    @Value("${rabbitmq.submission.routingkey.name}")
    String routingKeySubmission;

    
    @Bean
    public TopicExchange Exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue QueueLead() {
        return new Queue(queueLead);
    }

    @Bean
    public Binding BindingLead(TopicExchange exchange) {
        return BindingBuilder.bind(QueueLead()).to(exchange).with(routingKeyLead);
    }


    @Bean
    public Queue QueueSubmission() {
        return new Queue(queueSubmission);
    }

    @Bean
    public Binding validationBinding(TopicExchange exchange) {
        // Binds "validation.queue" to routing key "schema.validation"
        return BindingBuilder.bind(QueueSubmission())
                             .to(exchange)
                             .with(routingKeySubmission);
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
