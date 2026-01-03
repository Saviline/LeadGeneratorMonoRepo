package formschema.detail.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IPublisher;
import formschema.detail.messaging.events.SchemaUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FormSchemaProducer implements IPublisher {

    private final String exchange;
    private final String routingKey;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void PublishSchema(FormSchema schema) {
        SchemaUpdatedEvent event = SchemaUpdatedEvent.from(schema);

        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Published formschema.schema.updated: eventId={}, schemaId={}, customerId={}",
                event.getEventId(), schema.getId(), schema.getCustomerId());
    }
}
