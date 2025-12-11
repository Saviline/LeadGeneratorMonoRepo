package submission.detail;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class FormSchemaListener {

    Logger logger = org.slf4j.LoggerFactory.getLogger(FormSchemaListener.class);

    @RabbitListener(queues = "${rabbitmq.queue.name}") 
    void handleMessage(String message) {
        // Process the incoming message
        logger.info("Received message: {}", message);
    }
}
