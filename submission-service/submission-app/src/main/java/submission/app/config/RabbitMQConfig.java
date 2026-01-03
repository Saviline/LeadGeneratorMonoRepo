package submission.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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
    public Queue campaignQueue() {
        return QueueBuilder
                .durable(properties.getCampaignQueue().getQueue())
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
    public Binding campaignBinding(Queue campaignQueue, TopicExchange leadgenExchange) {
        return BindingBuilder
                .bind(campaignQueue)
                .to(leadgenExchange)
                .with(properties.getCampaignQueue().getRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
