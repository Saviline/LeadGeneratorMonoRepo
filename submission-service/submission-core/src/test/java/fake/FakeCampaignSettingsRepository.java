package fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import submission.core.domain.CampaignSettings;
import submission.core.ports.ICampaignSettingsRepository;

public class FakeCampaignSettingsRepository implements ICampaignSettingsRepository {

    Map<String, CampaignSettings> repository = new HashMap<>();

    @Override
    public void save(String customerId, String campaignId, CampaignSettings settings) {
        repository.put(buildKey(customerId, campaignId), settings);
    }

    @Override
    public Optional<CampaignSettings> findByCustomerIdAndCampaignId(String customerId, String campaignId) {
        return Optional.ofNullable(repository.get(buildKey(customerId, campaignId)));
    }

    @Override
    public void delete(String customerId, String campaignId) {
        repository.remove(buildKey(customerId, campaignId));
    }

    private String buildKey(String customerId, String campaignId) {
        return customerId + ":" + campaignId;
    }
}
