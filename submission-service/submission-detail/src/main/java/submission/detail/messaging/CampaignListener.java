package submission.detail.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.core.domain.CampaignSettings;
import submission.core.ports.ICampaignSettingsRepository;
import submission.detail.messaging.events.CampaignUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CampaignListener {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final ICampaignSettingsRepository campaignSettingsRepository;

    @RabbitListener(queues = "${rabbitmq.campaign-queue.queue}")
    public void handleCampaignUpdated(CampaignUpdatedEvent event) {
        log.info("Received campaign.campaign.updated: eventId={}, campaignId={}, customerId={}, status={}",
                event.getEventId(), event.getCampaignId(), event.getCustomerId(), event.getStatus());

        try {
            if (ACTIVE_STATUS.equals(event.getStatus())) {
                CampaignSettings settings = CampaignSettings.builder()
                        .formSchemaId(event.getFormSchemaId())
                        .integrationIds(event.getIntegrationIds())
                        .maxSubmissions(event.getMaxSubmissions())
                        .allowDuplicateSubmissions(event.isAllowDuplicateSubmissions())
                        .requireEmailVerification(event.isRequireEmailVerification())
                        .requirePhoneVerification(event.isRequirePhoneVerification())
                        .build();

                campaignSettingsRepository.save(event.getCustomerId(), event.getCampaignId(), settings);
                log.info("Saved active CampaignSettings: customerId={}, campaignId={}",
                        event.getCustomerId(), event.getCampaignId());
            } else {
                campaignSettingsRepository.delete(event.getCustomerId(), event.getCampaignId());
                log.info("Removed inactive CampaignSettings: customerId={}, campaignId={}, status={}",
                        event.getCustomerId(), event.getCampaignId(), event.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing CampaignUpdatedEvent: eventId={}, campaignId={}, error={}",
                    event.getEventId(), event.getCampaignId(), e.getMessage());
        }
    }
}
