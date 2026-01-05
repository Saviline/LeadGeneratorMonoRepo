package campaign.core.application;

import campaign.core.application.exceptions.SchemaNotFoundException;
import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;
import campaign.core.ports.outbound.ICampaignRepository;
import campaign.core.ports.outbound.ICustomerServiceClient;
import campaign.core.ports.outbound.IFormSchemaRepository;
import campaign.core.ports.outbound.IPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CampaignService {

    private final ICampaignRepository campaignRepository;
    private final IFormSchemaRepository formSchemaRepository;
    private final ICustomerServiceClient customerServiceClient;
    private final IPublisher publisher;

    public Mono<Campaign> createCampaign(Campaign campaign, String customerId) {
        log.debug("Creating campaign: name={}, customerId={}, formSchemaId={}",
                campaign.getDisplayName(), customerId, campaign.getFormSchemaId());

        return validateFormSchema(campaign.getFormSchemaId(), customerId)
                .then(validateIntegrations(campaign.getIntegrationIds(), customerId))
                .then(Mono.defer(() -> {
                    campaign.setStatus(CampaignStatus.DRAFT);
                    campaign.setCustomerId(customerId);
                    campaign.setId(UUID.randomUUID().toString());

                    log.debug("Saving campaign: id={}", campaign.getId());
                    return campaignRepository.save(campaign);
                }))
                .doOnSuccess(savedCampaign -> log.info("Campaign created: id={}", savedCampaign.getId()));
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
                .flatMap(savedCampaign -> {
                    if (status == CampaignStatus.ACTIVE) {
                        log.info("Publishing active campaign: id={}", savedCampaign.getId());
                        return publisher.publishCampaignUpdated(savedCampaign)
                                .thenReturn(savedCampaign);
                    }
                    return Mono.just(savedCampaign);
                });
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

    private Mono<Boolean> validateIntegrations(List<String> integrationIds, String customerId) {
        if (integrationIds == null || integrationIds.isEmpty()) {
            return Mono.just(true);
        }

        return customerServiceClient.getIntegrationEndpoints(customerId)
                .doOnNext(available -> log.debug("Available integrations for customer {}: {}", customerId, available))
                .flatMap(availableIntegrations -> {
                    List<String> invalidIntegrations = integrationIds.stream()
                            .filter(id -> !availableIntegrations.contains(id))
                            .toList();

                    if (!invalidIntegrations.isEmpty()) {
                        log.error("Invalid integrations: {} for customerId={}", invalidIntegrations, customerId);
                        return Mono.error(new IllegalArgumentException(
                                "Invalid integration IDs: " + invalidIntegrations));
                    }
                    return Mono.just(true);
                });
    }
}
