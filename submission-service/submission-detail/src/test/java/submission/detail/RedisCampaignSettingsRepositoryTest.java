package submission.detail;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import submission.core.domain.CampaignSettings;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class RedisCampaignSettingsRepositoryTest {

    private static final String CUSTOMER_ID = "customer-123";
    private static final String CAMPAIGN_ID = "campaign-456";
    private static final String SCHEMA_ID = "schema-789";

    @Container
    static RedisContainer redisContainer = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7.0"));

    private RedisTemplate<String, String> redisTemplate;
    private LettuceConnectionFactory connectionFactory;
    private RedisCampaignSettingsRepository repository;

    @BeforeEach
    void setUp() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                redisContainer.getHost(),
                redisContainer.getFirstMappedPort()
        );

        connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        repository = new RedisCampaignSettingsRepository(redisTemplate);
    }

    @AfterEach
    void tearDown() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void saveAndRetrieve_ShouldPersistAllFields() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .maxSubmissions(100)
                .allowDuplicateSubmissions(true)
                .requireEmailVerification(true)
                .requirePhoneVerification(false)
                .build();

        repository.save(CUSTOMER_ID, CAMPAIGN_ID, settings);

        Optional<CampaignSettings> found = repository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID);

        assertTrue(found.isPresent());
        assertEquals(SCHEMA_ID, found.get().getFormSchemaId());
        assertEquals(100, found.get().getMaxSubmissions());
        assertTrue(found.get().isAllowDuplicateSubmissions());
        assertTrue(found.get().isRequireEmailVerification());
        assertFalse(found.get().isRequirePhoneVerification());
    }

    @Test
    void findByCustomerIdAndCampaignId_NotFound_ShouldReturnEmpty() {
        Optional<CampaignSettings> found = repository.findByCustomerIdAndCampaignId("unknown", "unknown");

        assertTrue(found.isEmpty());
    }

    @Test
    void delete_ShouldRemoveSettings() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .maxSubmissions(50)
                .build();

        repository.save(CUSTOMER_ID, CAMPAIGN_ID, settings);

        Optional<CampaignSettings> beforeDelete = repository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID);
        assertTrue(beforeDelete.isPresent());

        repository.delete(CUSTOMER_ID, CAMPAIGN_ID);

        Optional<CampaignSettings> afterDelete = repository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID);
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void save_WithIntegrations_ShouldPersistAndRetrieve() {
        List<String> integrationIds = List.of("integration-1", "integration-2", "integration-3");

        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .maxSubmissions(200)
                .integrationIds(integrationIds)
                .build();

        repository.save(CUSTOMER_ID, CAMPAIGN_ID, settings);

        Optional<CampaignSettings> found = repository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID);

        assertTrue(found.isPresent());
        assertNotNull(found.get().getIntegrationIds());
        assertEquals(3, found.get().getIntegrationIds().size());
        assertTrue(found.get().getIntegrationIds().contains("integration-1"));
        assertTrue(found.get().getIntegrationIds().contains("integration-2"));
        assertTrue(found.get().getIntegrationIds().contains("integration-3"));
    }

    @Test
    void save_ShouldOverwriteExisting() {
        CampaignSettings original = CampaignSettings.builder()
                .formSchemaId("original-schema")
                .maxSubmissions(10)
                .build();

        repository.save(CUSTOMER_ID, CAMPAIGN_ID, original);

        CampaignSettings updated = CampaignSettings.builder()
                .formSchemaId("updated-schema")
                .maxSubmissions(200)
                .build();

        repository.save(CUSTOMER_ID, CAMPAIGN_ID, updated);

        Optional<CampaignSettings> found = repository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID);

        assertTrue(found.isPresent());
        assertEquals("updated-schema", found.get().getFormSchemaId());
        assertEquals(200, found.get().getMaxSubmissions());
    }
}
