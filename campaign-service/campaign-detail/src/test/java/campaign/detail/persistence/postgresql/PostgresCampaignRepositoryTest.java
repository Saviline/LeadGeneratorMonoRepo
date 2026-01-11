package campaign.detail.persistence.postgresql;

import campaign.core.domain.Campaign;
import campaign.core.domain.CampaignStatus;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PostgresCampaignRepositoryTest {

    private static final String CUSTOMER_ID = UUID.randomUUID().toString();
    private static final String OTHER_CUSTOMER_ID = UUID.randomUUID().toString();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private PostgresCampaignRepository repository;
    private ConnectionFactory connectionFactory;

    @BeforeEach
    void setUp() {
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host(postgres.getHost())
                .port(postgres.getFirstMappedPort())
                .database(postgres.getDatabaseName())
                .username(postgres.getUsername())
                .password(postgres.getPassword())
                .build();

        connectionFactory = new PostgresqlConnectionFactory(config);
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        repository = new PostgresCampaignRepository(template);

        Mono.from(connectionFactory.create())
                .flatMap(connection ->
                        ScriptUtils.executeSqlScript(connection, new ClassPathResource("schema.sql"))
                                .then(Mono.from(connection.close()))
                )
                .block();
    }

    @AfterEach
    void tearDown() {
        DatabaseClient client = DatabaseClient.create(connectionFactory);
        client.sql("DELETE FROM campaign_integrations").then()
                .then(client.sql("DELETE FROM campaigns").then())
                .block();
    }

    @Test
    void save_NewCampaign_ShouldPersistAndReturn() {
        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("Test Campaign")
                .description("Test Description")
                .status(CampaignStatus.DRAFT)
                .maxSubmissions(100)
                .build();

        StepVerifier.create(repository.save(campaign))
                .assertNext(saved -> {
                    assertEquals(campaign.getId(), saved.getId());
                    assertEquals("Test Campaign", saved.getDisplayName());
                    assertEquals(CampaignStatus.DRAFT, saved.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void findById_ExistingCampaign_ShouldReturn() {
        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("Find Me")
                .status(CampaignStatus.ACTIVE)
                .build();

        repository.save(campaign).block();

        StepVerifier.create(repository.findById(campaign.getId(), CUSTOMER_ID))
                .assertNext(found -> {
                    assertEquals(campaign.getId(), found.getId());
                    assertEquals("Find Me", found.getDisplayName());
                })
                .verifyComplete();
    }

    @Test
    void findById_WrongCustomer_ShouldReturnEmpty() {
        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("Secret Campaign")
                .status(CampaignStatus.DRAFT)
                .build();

        repository.save(campaign).block();

        StepVerifier.create(repository.findById(campaign.getId(), OTHER_CUSTOMER_ID))
                .verifyComplete();
    }

    @Test
    void findAllByCustomerId_MultipleCampaigns_ShouldReturnAll() {
        Campaign campaign1 = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("Campaign 1")
                .status(CampaignStatus.DRAFT)
                .build();

        Campaign campaign2 = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("Campaign 2")
                .status(CampaignStatus.ACTIVE)
                .build();

        Campaign otherCustomerCampaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(OTHER_CUSTOMER_ID)
                .displayName("Other Campaign")
                .status(CampaignStatus.DRAFT)
                .build();

        repository.save(campaign1).block();
        repository.save(campaign2).block();
        repository.save(otherCustomerCampaign).block();

        StepVerifier.create(repository.findAllByCustomerId(CUSTOMER_ID).collectList())
                .assertNext(campaigns -> {
                    assertEquals(2, campaigns.size());
                })
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldRemoveCampaign() {
        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("To Delete")
                .status(CampaignStatus.DRAFT)
                .build();

        repository.save(campaign).block();

        StepVerifier.create(repository.deleteById(campaign.getId(), CUSTOMER_ID))
                .verifyComplete();

        StepVerifier.create(repository.findById(campaign.getId(), CUSTOMER_ID))
                .verifyComplete();
    }

    @Test
    void existsById_ExistingCampaign_ShouldReturnTrue() {
        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("Exists")
                .status(CampaignStatus.DRAFT)
                .build();

        repository.save(campaign).block();

        StepVerifier.create(repository.existsById(campaign.getId(), CUSTOMER_ID))
                .assertNext(exists -> assertTrue(exists))
                .verifyComplete();
    }

    @Test
    void existsById_NonExistent_ShouldReturnFalse() {
        StepVerifier.create(repository.existsById(UUID.randomUUID().toString(), CUSTOMER_ID))
                .assertNext(exists -> assertFalse(exists))
                .verifyComplete();
    }

    @Test
    void save_WithIntegrations_ShouldPersistIntegrations() {
        List<String> integrationIds = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .displayName("With Integrations")
                .status(CampaignStatus.DRAFT)
                .integrationIds(integrationIds)
                .build();

        repository.save(campaign).block();

        StepVerifier.create(repository.findById(campaign.getId(), CUSTOMER_ID))
                .assertNext(found -> {
                    assertEquals(2, found.getIntegrationIds().size());
                })
                .verifyComplete();
    }
}
