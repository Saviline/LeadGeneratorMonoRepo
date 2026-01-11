package campaign.core.fake;

import campaign.core.domain.Campaign;
import campaign.core.ports.outbound.IPublisher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class FakePublisher implements IPublisher {

    private final List<Campaign> publishedCampaigns = new ArrayList<>();

    @Override
    public Mono<Void> publishCampaignUpdated(Campaign campaign) {
        publishedCampaigns.add(campaign);
        return Mono.empty();
    }

    public List<Campaign> getPublishedCampaigns() {
        return new ArrayList<>(publishedCampaigns);
    }

    public boolean hasPublished(String campaignId) {
        return publishedCampaigns.stream()
                .anyMatch(c -> c.getId().equals(campaignId));
    }

    public void clear() {
        publishedCampaigns.clear();
    }
}
