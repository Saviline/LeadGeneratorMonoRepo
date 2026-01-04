package campaign.detail.persistence.postgresql;

import campaign.core.domain.Campaign;
import campaign.core.ports.outbound.ICampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostgresCampaignRepository implements ICampaignRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Campaign> save(Campaign campaign) {
        log.debug("Saving campaign: {}", campaign.getDisplayName());
        CampaignEntity entity = CampaignMapper.toEntity(campaign);
        UUID campaignId = entity.getId();

        return template.selectOne(
                        query(where("campaign_id").is(campaignId)),
                        CampaignEntity.class)
                .flatMap(existing -> template.update(entity))
                .switchIfEmpty(template.insert(entity))
                .flatMap(savedEntity -> saveIntegrations(campaignId, campaign.getIntegrationIds())
                        .thenReturn(savedEntity))
                .map(savedEntity -> CampaignMapper.toDomain(savedEntity, campaign.getIntegrationIds()));
    }

    private Mono<Void> saveIntegrations(UUID campaignId, List<String> integrationIds) {
        return template.delete(
                        query(where("campaign_id").is(campaignId)),
                        CampaignIntegrationEntity.class)
                .then(Mono.defer(() -> {
                    if (integrationIds == null || integrationIds.isEmpty()) {
                        return Mono.empty();
                    }
                    List<CampaignIntegrationEntity> entities = CampaignMapper.toIntegrationEntities(campaignId, integrationIds);
                    return Flux.fromIterable(entities)
                            .flatMap(template::insert)
                            .then();
                }));
    }

    @Override
    public Mono<Campaign> findById(String id, String customerId) {
        log.debug("Finding campaign by id: {} for customer: {}", id, customerId);
        UUID campaignUuid = UUID.fromString(id);
        UUID customerUuid = UUID.fromString(customerId);

        return template.selectOne(
                        query(where("campaign_id").is(campaignUuid).and("customer_id").is(customerUuid)),
                        CampaignEntity.class)
                .flatMap(entity -> findIntegrationIds(campaignUuid)
                        .map(integrationIds -> CampaignMapper.toDomain(entity, integrationIds)));
    }

    private Mono<List<String>> findIntegrationIds(UUID campaignId) {
        return template.select(
                        query(where("campaign_id").is(campaignId)),
                        CampaignIntegrationEntity.class)
                .map(entity -> entity.getIntegrationId().toString())
                .collectList();
    }

    @Override
    public Flux<Campaign> findAllByCustomerId(String customerId) {
        log.debug("Finding all campaigns for customer: {}", customerId);
        UUID customerUuid = UUID.fromString(customerId);

        return template.select(
                        query(where("customer_id").is(customerUuid)),
                        CampaignEntity.class)
                .flatMap(entity -> findIntegrationIds(entity.getId())
                        .map(integrationIds -> CampaignMapper.toDomain(entity, integrationIds)));
    }

    @Override
    public Mono<Void> deleteById(String id, String customerId) {
        log.debug("Deleting campaign by id: {} for customer: {}", id, customerId);
        UUID campaignUuid = UUID.fromString(id);
        UUID customerUuid = UUID.fromString(customerId);

        return template.delete(
                        query(where("campaign_id").is(campaignUuid).and("customer_id").is(customerUuid)),
                        CampaignEntity.class)
                .then();
    }

    @Override
    public Mono<Boolean> existsById(String id, String customerId) {
        log.debug("Checking if campaign exists by id: {} for customer: {}", id, customerId);
        UUID campaignUuid = UUID.fromString(id);
        UUID customerUuid = UUID.fromString(customerId);

        return template.exists(
                query(where("campaign_id").is(campaignUuid).and("customer_id").is(customerUuid)),
                CampaignEntity.class);
    }
}
