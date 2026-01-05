package submission.core.ports;

import submission.core.domain.CampaignSettings;

import java.util.Optional;

public interface ICampaignSettingsRepository {
    void save(String customerId, String campaignId, CampaignSettings settings);
    Optional<CampaignSettings> findByCustomerIdAndCampaignId(String customerId, String campaignId);
    void delete(String customerId, String campaignId);
}
