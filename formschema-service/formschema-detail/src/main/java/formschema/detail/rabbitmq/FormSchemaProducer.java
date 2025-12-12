package formschema.detail.rabbitmq;

import java.util.logging.Logger;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import formschema.core.models.FormSchema;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FormSchemaProducer {

    //Use rabbitTemplate to send messages to RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    Logger logger = Logger.getLogger(FormSchemaProducer.class.getName());

    //Send a message to a specific queue
    public void sendFormSchemaMessage(String exchange,String routingKey, FormSchema schema){

        rabbitTemplate.convertAndSend(exchange, routingKey, schema);
        
        logger.info("Sent message to queue " + routingKey + ": " + schema.getName());
    }
}
