package campaign.core.ports.outbound;

import campaign.core.domain.Campaign;
import reactor.core.publisher.Mono;

public interface IPublisher {

    Mono<Void> publishCampaignUpdated(Campaign campaign);
}
