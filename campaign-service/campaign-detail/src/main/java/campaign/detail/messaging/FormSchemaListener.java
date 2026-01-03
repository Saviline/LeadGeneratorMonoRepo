package campaign.detail.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import campaign.core.domain.FormSchema;
import campaign.core.ports.outbound.IFormSchemaRepository;
import campaign.detail.messaging.events.FormSchemaUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FormSchemaListener {

    private final IFormSchemaRepository repository;

    @RabbitListener(queues = "${rabbitmq.form-schema-queue.queue}")
    public void handleFormSchemaUpdated(FormSchemaUpdatedEvent event) {
        log.info("Received formschema.schema.updated: eventId={}, schemaId={}, customerId={}",
                event.getEventId(), event.getSchemaId(), event.getCustomerId());

        try {
            FormSchema schema = FormSchema.builder()
                    .id(event.getSchemaId())
                    .customerId(event.getCustomerId())
                    .name(event.getName())
                    .version(event.getSchemaVersion())
                    .build();

            repository.saveFormSchema(schema, event.getCustomerId())
                    .doOnSuccess(saved -> log.info("Cached FormSchema: schemaId={}", saved.getId()))
                    .doOnError(error -> log.error("Failed to cache FormSchema: schemaId={}, error={}",
                            event.getSchemaId(), error.getMessage()))
                    .subscribe();
        } catch (Exception e) {
            log.error("Error processing FormSchemaUpdatedEvent: eventId={}, schemaId={}, error={}",
                    event.getEventId(), event.getSchemaId(), e.getMessage());
        }
    }
}
