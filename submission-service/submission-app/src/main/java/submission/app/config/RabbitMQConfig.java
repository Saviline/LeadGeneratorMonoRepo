package submission.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {
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

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
       Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }
}
