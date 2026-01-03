package submission.detail.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.core.domain.Submission;
import submission.core.ports.IPublish;
import submission.detail.messaging.events.SubmissionCreatedEvent;

@Slf4j
@RequiredArgsConstructor
public class SubmissionProducer implements IPublish<Submission> {

    private final String exchange;
    private final String routingKey;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Submission submission) {
        SubmissionCreatedEvent event = SubmissionCreatedEvent.from(submission);

        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Published submission.submission.created: eventId={}, submissionId={}, customerId={}, status={}",
                event.getEventId(), submission.getSubmissionId(), submission.getCustomerId(), submission.getStatus());
    }
}
