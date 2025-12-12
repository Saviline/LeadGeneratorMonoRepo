package formschema.detail.rabbitmq;

import java.util.logging.Logger;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import formschema.core.models.FormSchema;
import formschema.detail.SchemaTranslator;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FormSchemaProducer {
    //Routing key
    @Value("${rabbitmq.submission.routingkey.name}")
    String routingKeySubmission;

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    SchemaTranslator schemaTranslator = new SchemaTranslator();

    private final RabbitTemplate rabbitTemplate;

    Logger logger = Logger.getLogger(FormSchemaProducer.class.getName());

    //Send a message to a specific queue
    public void sendFormSchemaMessage(FormSchema schema){

        var validation = schemaTranslator.toValidationPayload(schema);
        
        rabbitTemplate.convertAndSend(exchange, routingKeySubmission, validation);
        
        logger.info("Sent message to queue " + routingKeySubmission + ": " + schema.getName());
    }
}
