package fake;
import java.util.HashMap;
import java.util.Map;

import submission.core.ports.IRepositoryCampaign;

public class FakeCampaignRepository implements IRepositoryCampaign {

    Map<String, String> repository = new HashMap<>();

    @Override
    public void saveCampaign(String CampaignId) {
        repository.put(CampaignId, CampaignId);
    }

    @Override
    public String getCampaignById(String CampaignId) {
        return repository.get(CampaignId);
    }
}
