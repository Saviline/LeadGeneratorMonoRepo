package campaign.core.fake;

import campaign.core.domain.Campaign;
import campaign.core.ports.outbound.ICampaignRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeCampaignRepository implements ICampaignRepository {

    private final Map<String, Campaign> store = new ConcurrentHashMap<>();

    @Override
    public Mono<Campaign> save(Campaign campaign) {
        store.put(campaign.getId(), campaign);
        return Mono.just(campaign);
    }

    @Override
    public Mono<Campaign> findById(String id, String customerId) {
        Campaign campaign = store.get(id);
        if (campaign != null && campaign.getCustomerId().equals(customerId)) {
            return Mono.just(campaign);
        }
        return Mono.empty();
    }

    @Override
    public Flux<Campaign> findAllByCustomerId(String customerId) {
        return Flux.fromStream(store.values().stream()
                .filter(c -> c.getCustomerId().equals(customerId)));
    }

    @Override
    public Mono<Void> deleteById(String id, String customerId) {
        Campaign campaign = store.get(id);
        if (campaign != null && campaign.getCustomerId().equals(customerId)) {
            store.remove(id);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> existsById(String id, String customerId) {
        Campaign campaign = store.get(id);
        return Mono.just(campaign != null && campaign.getCustomerId().equals(customerId));
    }

    public void clear() {
        store.clear();
    }
}
