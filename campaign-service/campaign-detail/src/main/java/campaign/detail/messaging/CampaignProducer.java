package campaign.detail.messaging;

import campaign.core.domain.Campaign;
import campaign.core.ports.outbound.IPublisher;
import campaign.detail.messaging.events.CampaignUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
public class CampaignProducer implements IPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    @Override
    public Mono<Void> publishCampaignUpdated(Campaign campaign) {
        return Mono.fromRunnable(() -> {
            CampaignUpdatedEvent event = CampaignUpdatedEvent.from(campaign);
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Published campaign.campaign.updated: eventId={}, campaignId={}, customerId={}, status={}",
                    event.getEventId(), campaign.getId(), campaign.getCustomerId(), campaign.getStatus());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
