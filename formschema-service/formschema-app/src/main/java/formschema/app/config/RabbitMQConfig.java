package formschema.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //Exhange
    @Value("${rabbitmq.exchange.name}")
    String exchange;
    //Queue
    @Value("${rabbitmq.queue.name}")
    String queue;
    //Routing key
    @Value("${rabbitmq.routingkey.name}")
    String routingKey;
    
    @Bean
    public TopicExchange Exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue Queue() {
        return new Queue(queue);
    }

    @Bean
    public Binding Binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    // These are auto configured by Spring Boot
    //CachingConnectionFactory 
    //RabbitAdmin
    //RabbitTemplate
}
