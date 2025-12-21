package fake;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import submission.core.domain.Campaign;
import submission.core.ports.IRepositoryCampaign;

public class FakeCampaignRepository implements IRepositoryCampaign {

    Map<String, Campaign> repository = new HashMap<>();

    @Override
    public void save(Campaign campaign) {
        repository.put(campaign.getCampaignId(), campaign);
    }

    @Override
    public Optional<Campaign> findByIdAndCustomerId(String campaignId, String customerId) {
        Campaign campaign = repository.get(campaignId);
        if (campaign != null && campaign.getCustomerId().equals(customerId)) {
            return Optional.of(campaign);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String campaignId) {
        repository.remove(campaignId);
    }
}
