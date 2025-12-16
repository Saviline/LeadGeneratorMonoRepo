package submission.detail;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormSchemaListener {

    Logger logger = org.slf4j.LoggerFactory.getLogger(FormSchemaListener.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final RedisFormSchemaCache repository;

    @RabbitListener(queues = "${rabbitmq.queue.name}") 
    void handleMessage(Map<String, Object> payload) {

       try {
        logger.info("Payload received: " + payload); // Let's see what is actually inside

        String name = (String) payload.get("schemaId");
        Object validationSchema = payload.get("validationSchema");

        if (name == null) logger.error("CRITICAL: 'schemaId' is missing from payload!");
        if (validationSchema == null) logger.error("CRITICAL: 'validationSchema' is missing from payload!");

        String schemaJson = mapper.writeValueAsString(validationSchema);
        repository.save(name, schemaJson);
        
        logger.info("✅ Saved schema for form: " + name);

    } catch (Exception e) {
        logger.error("CRASHED while processing message", e);
    }
}
}
