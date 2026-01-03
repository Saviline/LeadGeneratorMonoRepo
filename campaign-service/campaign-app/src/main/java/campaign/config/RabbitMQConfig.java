package campaign.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    @Bean
    public TopicExchange leadgenExchange() {
        return ExchangeBuilder.topicExchange(properties.getExchange()).durable(true).build();
    }

    @Bean
    public Queue formSchemaQueue() {
        return QueueBuilder
                .durable(properties.getFormSchemaQueue().getQueue())
                .build();
    }

    @Bean
    public Binding formSchemaBinding(Queue formSchemaQueue, TopicExchange leadgenExchange) {
        return BindingBuilder
                .bind(formSchemaQueue)
                .to(leadgenExchange)
                .with(properties.getFormSchemaQueue().getRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
