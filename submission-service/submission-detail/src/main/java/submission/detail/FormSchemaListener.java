package submission.detail;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.RequiredArgsConstructor;
import submission.detail.dto.FormSchema;

@RequiredArgsConstructor
public class FormSchemaListener {

    Logger logger = org.slf4j.LoggerFactory.getLogger(FormSchemaListener.class);

   private final ReditRepository repository;

    @RabbitListener(queues = "${rabbitmq.queue.name}") 
    void handleMessage(FormSchema schema) {
        // Process the incoming message
        
        //Save the schema to the repository
        repository.saveFormSchema(schema);

        logger.info("Received message: {}", schema.getName());
    }
}
