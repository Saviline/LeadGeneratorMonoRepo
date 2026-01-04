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

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CampaignService {

    private final ICampaignRepository campaignRepository;
    private final IFormSchemaRepository formSchemaRepository;
    private final IPublisher publisher;

    public Mono<Campaign> createCampaign(Campaign campaign, String customerId) {
        log.debug("Creating campaign: name={}, customerId={}, formSchemaId={}",
                campaign.getDisplayName(), customerId, campaign.getFormSchemaId());

        return validateFormSchema(campaign.getFormSchemaId(), customerId)
                .then(Mono.defer(() -> {
                    campaign.setStatus(CampaignStatus.DRAFT);
                    campaign.setCustomerId(customerId);
                    campaign.setId(UUID.randomUUID().toString());

                    log.debug("Saving campaign: id={}", campaign.getId());
                    return campaignRepository.save(campaign);
                }))
                .flatMap(savedCampaign -> {
                    log.info("Publishing campaign created: id={}", savedCampaign.getId());
                    return publisher.publishCampaignUpdated(savedCampaign)
                            .thenReturn(savedCampaign);
                });
    }

    public Mono<Campaign> updateCampaign(String campaignId, Campaign campaign, String customerId) {
        log.debug("Updating campaign: id={}, customerId={}", campaignId, customerId);

        return campaignRepository.findById(campaignId, customerId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Campaign not found")))
                .flatMap(existing -> {
                    if (campaign.getFormSchemaId() != null &&
                        !campaign.getFormSchemaId().equals(existing.getFormSchemaId())) {
                        return validateFormSchema(campaign.getFormSchemaId(), customerId)
                                .thenReturn(existing);
                    }
                    return Mono.just(existing);
                })
                .flatMap(existing -> {
                    existing.setDisplayName(campaign.getDisplayName());
                    existing.setDescription(campaign.getDescription());
                    existing.setFormSchemaId(campaign.getFormSchemaId());
                    existing.setMaxSubmissions(campaign.getMaxSubmissions());
                    existing.setAllowDuplicateSubmissions(campaign.isAllowDuplicateSubmissions());
                    existing.setRequireEmailVerification(campaign.isRequireEmailVerification());
                    existing.setRequirePhoneVerification(campaign.isRequirePhoneVerification());
                    existing.setStartDate(campaign.getStartDate());
                    existing.setEndDate(campaign.getEndDate());
                    existing.setIntegrationIds(campaign.getIntegrationIds());
                    return campaignRepository.save(existing);
                })
                .flatMap(savedCampaign -> publisher.publishCampaignUpdated(savedCampaign)
                        .thenReturn(savedCampaign));
    }

    public Mono<Campaign> updateStatus(String campaignId, CampaignStatus status, String customerId) {
        log.debug("Updating campaign status: id={}, status={}, customerId={}", campaignId, status, customerId);

        return campaignRepository.findById(campaignId, customerId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Campaign not found")))
                .flatMap(existing -> {
                    existing.setStatus(status);
                    return campaignRepository.save(existing);
                })
                .flatMap(savedCampaign -> publisher.publishCampaignUpdated(savedCampaign)
                        .thenReturn(savedCampaign));
    }

    public Mono<Campaign> findById(String campaignId, String customerId) {
        log.debug("Finding campaign: id={}, customerId={}", campaignId, customerId);
        return campaignRepository.findById(campaignId, customerId);
    }

    public Flux<Campaign> findAllByCustomerId(String customerId) {
        log.debug("Finding all campaigns for customer: {}", customerId);
        return campaignRepository.findAllByCustomerId(customerId);
    }

    public Mono<Void> deleteById(String campaignId, String customerId) {
        log.debug("Deleting campaign: id={}, customerId={}", campaignId, customerId);
        return campaignRepository.deleteById(campaignId, customerId);
    }

    private Mono<Boolean> validateFormSchema(String formSchemaId, String customerId) {
        if (formSchemaId == null) {
            return Mono.just(true);
        }

        return formSchemaRepository.schemaExists(formSchemaId, customerId)
                .doOnNext(exists -> log.debug("Schema exists check: formSchemaId={}, customerId={}, exists={}",
                        formSchemaId, customerId, exists))
                .flatMap(exists -> {
                    if (!exists) {
                        log.error("Schema not found: formSchemaId={}, customerId={}", formSchemaId, customerId);
                        return Mono.error(new SchemaNotFoundException("Schema not found"));
                    }
                    return Mono.just(true);
                });
    }
}
