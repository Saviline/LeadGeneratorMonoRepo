package campaign.core.ports.outbound;

import campaign.core.domain.Campaign;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICampaignRepository {

    Mono<Campaign> save(Campaign campaign);

    Mono<Campaign> findById(String id, String customerId);

    Flux<Campaign> findAllByCustomerId(String customerId);

    Mono<Void> deleteById(String id, String customerId);

    Mono<Boolean> existsById(String id, String customerId);
}
