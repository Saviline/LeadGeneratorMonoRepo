package submission.core.ports;

import java.util.Optional;
import submission.core.domain.Campaign;

public interface IRepositoryCampaign {
    void save(Campaign campaign);
    Optional<Campaign> findByIdAndCustomerId(String campaignId, String customerId);
    void delete(String campaignId);
}
