package campaign.detail.persistence.postgresql;

import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;

import java.util.List;
import java.util.UUID;

public class CampaignMapper {

    public static CampaignEntity toEntity(Campaign campaign) {
        return CampaignEntity.builder()
                .id(campaign.getId() != null ? UUID.fromString(campaign.getId()) : UUID.randomUUID())
                .customerId(UUID.fromString(campaign.getCustomerId()))
                .displayName(campaign.getDisplayName())
                .description(campaign.getDescription())
                .formSchemaId(campaign.getFormSchemaId() != null ? UUID.fromString(campaign.getFormSchemaId()) : null)
                .status(campaign.getStatus() != null ? campaign.getStatus().name() : CampaignStatus.DRAFT.name())
                .maxSubmissions(campaign.getMaxSubmissions())
                .allowDuplicateSubmissions(campaign.isAllowDuplicateSubmissions())
                .requireEmailVerification(campaign.isRequireEmailVerification())
                .requirePhoneVerification(campaign.isRequirePhoneVerification())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }

    public static Campaign toDomain(CampaignEntity entity) {
        return toDomain(entity, null);
    }

    public static Campaign toDomain(CampaignEntity entity, List<String> integrationIds) {
        return Campaign.builder()
                .id(entity.getId().toString())
                .customerId(entity.getCustomerId().toString())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .formSchemaId(entity.getFormSchemaId() != null ? entity.getFormSchemaId().toString() : null)
                .status(entity.getStatus() != null ? CampaignStatus.valueOf(entity.getStatus()) : null)
                .maxSubmissions(entity.getMaxSubmissions())
                .allowDuplicateSubmissions(entity.isAllowDuplicateSubmissions())
                .requireEmailVerification(entity.isRequireEmailVerification())
                .requirePhoneVerification(entity.isRequirePhoneVerification())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .integrationIds(integrationIds)
                .build();
    }

    public static List<CampaignIntegrationEntity> toIntegrationEntities(UUID campaignId, List<String> integrationIds) {
        if (integrationIds == null || integrationIds.isEmpty()) {
            return List.of();
        }
        return integrationIds.stream()
                .map(integrationId -> CampaignIntegrationEntity.builder()
                        .campaignId(campaignId)
                        .integrationId(UUID.fromString(integrationId))
                        .build())
                .toList();
    }
}
