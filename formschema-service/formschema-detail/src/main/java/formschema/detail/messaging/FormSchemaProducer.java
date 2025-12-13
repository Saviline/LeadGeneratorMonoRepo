package formschema.detail.messaging;
import java.util.Map;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IPublisher;
import formschema.core.ports.outbound.ITranslator;
import formschema.detail.messaging.events.SchemaValidationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FormSchemaProducer implements IPublisher {

    private final String exchange;
    private final String validationRoutingKey;
    private final String businessRoutingKey;

    private final ITranslator<Map<String, Object>> schemaTranslator;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void PublishSchema(FormSchema schema) {
        String correlationId = java.util.UUID.randomUUID().toString();

        //Publish to submission
        publishValidationEvent(schema, correlationId);
    }

    private void publishValidationEvent(FormSchema schema, String correlationId){
        SchemaValidationUpdatedEvent event = SchemaValidationUpdatedEvent.builder()
        .eventId(java.util.UUID.randomUUID().toString())
        .eventType("schema.validation.updated")
        .timestamp(java.time.Instant.now())
        .correlationId(correlationId)
        .schemaId(schema.getId())
        .schemaVersion(schema.getVersion())
        .validationSchema(schemaTranslator.convertToValidationSchema(schema).toString())
        .build();

        rabbitTemplate.convertAndSend(exchange,  validationRoutingKey, event);
        log.info("Published schema.validation.updated: schemaId={}", schema.getId());
    }
}
