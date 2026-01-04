package campaign.detail.persistence.postgresql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("campaign_integrations")
public class CampaignIntegrationEntity {

    @Column("campaign_id")
    private UUID campaignId;

    @Column("integration_id")
    private UUID integrationId;
}
