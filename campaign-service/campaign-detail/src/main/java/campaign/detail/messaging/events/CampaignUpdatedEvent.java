package campaign.detail.messaging.events;

import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampaignUpdatedEvent extends EventBase {

    private String campaignId;
    private String displayName;
    private String description;
    private String formSchemaId;
    private CampaignStatus status;
    private Integer maxSubmissions;
    private boolean allowDuplicateSubmissions;
    private boolean requireEmailVerification;
    private boolean requirePhoneVerification;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> integrationIds;

    public static CampaignUpdatedEvent from(Campaign campaign) {
        return CampaignUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("CAMPAIGN_UPDATED")
                .timestamp(Instant.now())
                .source("campaign-service")
                .customerId(campaign.getCustomerId())
                .campaignId(campaign.getId())
                .displayName(campaign.getDisplayName())
                .description(campaign.getDescription())
                .formSchemaId(campaign.getFormSchemaId())
                .status(campaign.getStatus())
                .maxSubmissions(campaign.getMaxSubmissions())
                .allowDuplicateSubmissions(campaign.isAllowDuplicateSubmissions())
                .requireEmailVerification(campaign.isRequireEmailVerification())
                .requirePhoneVerification(campaign.isRequirePhoneVerification())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .integrationIds(campaign.getIntegrationIds())
                .build();
    }
}
