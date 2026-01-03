package submission.detail.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.core.ports.ICacheFormSchema;
import submission.detail.messaging.events.FormSchemaUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FormSchemaListener {

    private final ICacheFormSchema formSchemaCache;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.form-schema-queue.queue}")
    public void handleFormSchemaUpdated(FormSchemaUpdatedEvent event) {
        log.info("Received formschema.schema.updated: eventId={}, schemaId={}, customerId={}",
                event.getEventId(), event.getSchemaId(), event.getCustomerId());

        try {
            String validationSchemaJson = objectMapper.writeValueAsString(event.getFields());
            formSchemaCache.save(event.getSchemaId(), validationSchemaJson);

            log.info("Cached FormSchema: schemaId={}", event.getSchemaId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize FormSchema fields: eventId={}, schemaId={}, error={}",
                    event.getEventId(), event.getSchemaId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error processing FormSchemaUpdatedEvent: eventId={}, schemaId={}, error={}",
                    event.getEventId(), event.getSchemaId(), e.getMessage());
        }
    }
}
