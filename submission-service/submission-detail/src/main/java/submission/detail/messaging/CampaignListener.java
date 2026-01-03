package submission.detail.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.core.domain.Campaign;
import submission.core.ports.IRepositoryCampaign;
import submission.detail.messaging.events.CampaignUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CampaignListener {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final IRepositoryCampaign campaignRepository;

    @RabbitListener(queues = "${rabbitmq.campaign-queue.queue}")
    public void handleCampaignUpdated(CampaignUpdatedEvent event) {
        log.info("Received campaign.campaign.updated: eventId={}, campaignId={}, customerId={}, status={}",
                event.getEventId(), event.getCampaignId(), event.getCustomerId(), event.getStatus());

        try {
            if (ACTIVE_STATUS.equals(event.getStatus())) {
                Campaign campaign = Campaign.builder()
                        .campaignId(event.getCampaignId())
                        .customerId(event.getCustomerId())
                        .formSchemaId(event.getFormSchemaId())
                        .build();

                campaignRepository.save(campaign);
                log.info("Cached active Campaign: campaignId={}", event.getCampaignId());
            } else {
                campaignRepository.delete(event.getCampaignId());
                log.info("Removed inactive Campaign from cache: campaignId={}, status={}",
                        event.getCampaignId(), event.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing CampaignUpdatedEvent: eventId={}, campaignId={}, error={}",
                    event.getEventId(), event.getCampaignId(), e.getMessage());
        }
    }
}
