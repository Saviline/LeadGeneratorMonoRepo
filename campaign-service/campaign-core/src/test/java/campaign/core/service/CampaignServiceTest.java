package campaign.core.service;

import campaign.core.application.CampaignService;
import campaign.core.application.exceptions.SchemaNotFoundException;
import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;
import campaign.core.fake.FakeCampaignRepository;
import campaign.core.fake.FakeCustomerServiceClient;
import campaign.core.fake.FakeFormSchemaRepository;
import campaign.core.fake.FakePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CampaignServiceTest {

    private static final String CUSTOMER_ID = "customer-123";
    private static final String SCHEMA_ID = "schema-456";

    private FakeCampaignRepository campaignRepository;
    private FakeFormSchemaRepository formSchemaRepository;
    private FakeCustomerServiceClient customerServiceClient;
    private FakePublisher publisher;
    private CampaignService service;

    @BeforeEach
    void setUp() {
        campaignRepository = new FakeCampaignRepository();
        formSchemaRepository = new FakeFormSchemaRepository();
        customerServiceClient = new FakeCustomerServiceClient();
        publisher = new FakePublisher();
        service = new CampaignService(campaignRepository, formSchemaRepository,
                customerServiceClient, publisher);
    }

    @Test
    void createCampaign_ValidSchema_ShouldSetDraftStatusAndSave() {
        formSchemaRepository.addSchema(SCHEMA_ID, CUSTOMER_ID);

        Campaign campaign = Campaign.builder()
                .displayName("Test Campaign")
                .formSchemaId(SCHEMA_ID)
                .build();

        StepVerifier.create(service.createCampaign(campaign, CUSTOMER_ID))
                .assertNext(saved -> {
                    assertNotNull(saved.getId(), "Campaign should have ID assigned");
                    assertEquals(CUSTOMER_ID, saved.getCustomerId());
                    assertEquals(CampaignStatus.DRAFT, saved.getStatus());
                    assertEquals("Test Campaign", saved.getDisplayName());
                })
                .verifyComplete();
    }

    @Test
    void createCampaign_InvalidSchema_ShouldThrowSchemaNotFoundException() {
        Campaign campaign = Campaign.builder()
                .displayName("Test Campaign")
                .formSchemaId("non-existent-schema")
                .build();

        StepVerifier.create(service.createCampaign(campaign, CUSTOMER_ID))
                .expectError(SchemaNotFoundException.class)
                .verify();
    }

    @Test
    void createCampaign_NullSchema_ShouldSucceed() {
        Campaign campaign = Campaign.builder()
                .displayName("Campaign Without Schema")
                .build();

        StepVerifier.create(service.createCampaign(campaign, CUSTOMER_ID))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertNull(saved.getFormSchemaId());
                    assertEquals(CampaignStatus.DRAFT, saved.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void updateStatus_ToActive_ShouldPublishCampaign() {
        formSchemaRepository.addSchema(SCHEMA_ID, CUSTOMER_ID);

        Campaign campaign = Campaign.builder()
                .displayName("Test Campaign")
                .formSchemaId(SCHEMA_ID)
                .build();

        String campaignId = service.createCampaign(campaign, CUSTOMER_ID).block().getId();
        publisher.clear();

        StepVerifier.create(service.updateStatus(campaignId, CampaignStatus.ACTIVE, CUSTOMER_ID))
                .assertNext(updated -> {
                    assertEquals(CampaignStatus.ACTIVE, updated.getStatus());
                    assertTrue(publisher.hasPublished(campaignId), "Campaign should be published");
                })
                .verifyComplete();
    }

    @Test
    void updateStatus_ToPaused_ShouldNotPublish() {
        Campaign campaign = Campaign.builder()
                .displayName("Test Campaign")
                .build();

        String campaignId = service.createCampaign(campaign, CUSTOMER_ID).block().getId();
        publisher.clear();

        StepVerifier.create(service.updateStatus(campaignId, CampaignStatus.PAUSED, CUSTOMER_ID))
                .assertNext(updated -> {
                    assertEquals(CampaignStatus.PAUSED, updated.getStatus());
                    assertFalse(publisher.hasPublished(campaignId), "Campaign should not be published for PAUSED");
                })
                .verifyComplete();
    }

    @Test
    void updateStatus_NonExistent_ShouldThrowIllegalArgumentException() {
        StepVerifier.create(service.updateStatus("non-existent", CampaignStatus.ACTIVE, CUSTOMER_ID))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findById_ExistingCampaign_ShouldReturnCampaign() {
        Campaign campaign = Campaign.builder()
                .displayName("Test Campaign")
                .build();

        String campaignId = service.createCampaign(campaign, CUSTOMER_ID).block().getId();

        StepVerifier.create(service.findById(campaignId, CUSTOMER_ID))
                .assertNext(found -> {
                    assertEquals(campaignId, found.getId());
                    assertEquals("Test Campaign", found.getDisplayName());
                })
                .verifyComplete();
    }

    @Test
    void findById_NonExistent_ShouldReturnEmpty() {
        StepVerifier.create(service.findById("non-existent", CUSTOMER_ID))
                .verifyComplete();
    }

    @Test
    void findAllByCustomerId_MultipleCampaigns_ShouldReturnAll() {
        Campaign campaign1 = Campaign.builder().displayName("Campaign 1").build();
        Campaign campaign2 = Campaign.builder().displayName("Campaign 2").build();

        service.createCampaign(campaign1, CUSTOMER_ID).block();
        service.createCampaign(campaign2, CUSTOMER_ID).block();

        StepVerifier.create(service.findAllByCustomerId(CUSTOMER_ID).collectList())
                .assertNext(campaigns -> {
                    assertEquals(2, campaigns.size());
                })
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldRemoveCampaign() {
        Campaign campaign = Campaign.builder()
                .displayName("To Delete")
                .build();

        String campaignId = service.createCampaign(campaign, CUSTOMER_ID).block().getId();

        StepVerifier.create(service.deleteById(campaignId, CUSTOMER_ID))
                .verifyComplete();

        StepVerifier.create(service.findById(campaignId, CUSTOMER_ID))
                .verifyComplete();
    }

    @Test
    void createCampaign_WithValidIntegrations_ShouldSucceed() {
        customerServiceClient.setIntegrations(CUSTOMER_ID, List.of("integration-1", "integration-2"));

        Campaign campaign = Campaign.builder()
                .displayName("Campaign With Integrations")
                .integrationIds(List.of("integration-1"))
                .build();

        StepVerifier.create(service.createCampaign(campaign, CUSTOMER_ID))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals(List.of("integration-1"), saved.getIntegrationIds());
                })
                .verifyComplete();
    }

    @Test
    void createCampaign_WithInvalidIntegrations_ShouldThrowIllegalArgumentException() {
        customerServiceClient.setIntegrations(CUSTOMER_ID, List.of("integration-1"));

        Campaign campaign = Campaign.builder()
                .displayName("Campaign With Invalid Integration")
                .integrationIds(List.of("invalid-integration"))
                .build();

        StepVerifier.create(service.createCampaign(campaign, CUSTOMER_ID))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
