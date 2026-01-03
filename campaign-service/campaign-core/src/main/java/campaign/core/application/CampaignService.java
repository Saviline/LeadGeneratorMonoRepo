package campaign.core.application;

import campaign.core.application.exceptions.SchemaNotFoundException;
import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;
import campaign.core.ports.outbound.ICampaignRepository;
import campaign.core.ports.outbound.IFormSchemaRepository;
import campaign.core.ports.outbound.IPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CampaignService {

    private final ICampaignRepository campaignRepository;
    private final IFormSchemaRepository formSchemaRepository;
    private final IPublisher publisher;

    public Mono<Campaign> createCampaign(Campaign campaign, String customerId) {
        log.debug("Creating campaign: name={}, customerId={}, formSchemaId={}",
                campaign.getName(), customerId, campaign.getFormSchemaId());

        return formSchemaRepository.schemaExists(campaign.getFormSchemaId(), customerId)
                .doOnNext(exists -> log.debug("Schema exists check: formSchemaId={}, customerId={}, exists={}",
                        campaign.getFormSchemaId(), customerId, exists))
                .filter(exists -> exists)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Schema not found: formSchemaId={}, customerId={}", campaign.getFormSchemaId(), customerId);
                    return Mono.error(new SchemaNotFoundException("Schema not found"));
                }))
                .then(Mono.defer(() -> {
                    campaign.setStatus(CampaignStatus.DRAFT);
                    campaign.setCustomerId(customerId);
                    campaign.setId(UUID.randomUUID().toString());

                    log.debug("Saving campaign: id={}", campaign.getId());

                    return campaignRepository.save(campaign);
                }))
                .flatMap(savedCampaign -> {
                    log.info("Publishing campaign: id={}", savedCampaign.getId());
                    return publisher.publishCampaignUpdated(savedCampaign)
                            .thenReturn(savedCampaign);
                });
    }
}
