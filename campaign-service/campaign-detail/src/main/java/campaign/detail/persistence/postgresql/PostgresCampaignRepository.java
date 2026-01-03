package campaign.detail.persistence.postgresql;

import campaign.core.domain.Campaign;
import campaign.core.ports.outbound.ICampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostgresCampaignRepository implements ICampaignRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Campaign> save(Campaign campaign) {
        log.debug("Saving campaign: {}", campaign.getName());
        CampaignEntity entity = CampaignMapper.toEntity(campaign);

        return template.selectOne(
                        query(where("id").is(entity.getId())),
                        CampaignEntity.class)
                .flatMap(existing -> template.update(entity))
                .switchIfEmpty(template.insert(entity))
                .map(CampaignMapper::toDomain);
    }

    @Override
    public Mono<Campaign> findById(String id, String customerId) {
        log.debug("Finding campaign by id: {} for customer: {}", id, customerId);
        return template.selectOne(
                        query(where("id").is(id).and("customer_id").is(customerId)),
                        CampaignEntity.class)
                .map(CampaignMapper::toDomain);
    }

    @Override
    public Flux<Campaign> findAllByCustomerId(String customerId) {
        log.debug("Finding all campaigns for customer: {}", customerId);
        return template.select(
                        query(where("customer_id").is(customerId)),
                        CampaignEntity.class)
                .map(CampaignMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id, String customerId) {
        log.debug("Deleting campaign by id: {} for customer: {}", id, customerId);
        return template.delete(
                        query(where("id").is(id).and("customer_id").is(customerId)),
                        CampaignEntity.class)
                .then();
    }

    @Override
    public Mono<Boolean> existsById(String id, String customerId) {
        log.debug("Checking if campaign exists by id: {} for customer: {}", id, customerId);
        return template.exists(
                query(where("id").is(id).and("customer_id").is(customerId)),
                CampaignEntity.class);
    }
}
