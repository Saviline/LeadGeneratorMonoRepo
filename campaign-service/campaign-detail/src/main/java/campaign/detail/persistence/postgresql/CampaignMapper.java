package campaign.detail.persistence.postgresql;

import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;

import java.util.UUID;

public class CampaignMapper {

    public static CampaignEntity toEntity(Campaign campaign) {
        return CampaignEntity.builder()
                .id(campaign.getId() != null ? campaign.getId() : UUID.randomUUID().toString())
                .customerId(campaign.getCustomerId())
                .name(campaign.getName())
                .formSchemaId(campaign.getFormSchemaId())
                .status(campaign.getStatus() != null ? campaign.getStatus().name() : CampaignStatus.DRAFT.name())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }

    public static Campaign toDomain(CampaignEntity entity) {
        return Campaign.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .name(entity.getName())
                .formSchemaId(entity.getFormSchemaId())
                .status(entity.getStatus() != null ? CampaignStatus.valueOf(entity.getStatus()) : null)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
