package submission.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import submission.core.domain.CampaignSettings;
import submission.core.ports.ICampaignSettingsRepository;

import java.util.List;
import java.util.Optional;

//Claude: Where are we configuring Persistence and what method to use and all other configurations?

@RequiredArgsConstructor
public class RedisCampaignSettingsRepository implements ICampaignSettingsRepository {

    private static final String KEY_PREFIX = "campaign:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String customerId, String campaignId, CampaignSettings settings) {
        String key = buildKey(customerId, campaignId);
        redisTemplate.opsForHash().put(key, "formSchemaId", settings.getFormSchemaId());
        redisTemplate.opsForHash().put(key, "maxSubmissions", String.valueOf(settings.getMaxSubmissions()));
        redisTemplate.opsForHash().put(key, "allowDuplicateSubmissions", String.valueOf(settings.isAllowDuplicateSubmissions()));
        redisTemplate.opsForHash().put(key, "requireEmailVerification", String.valueOf(settings.isRequireEmailVerification()));
        redisTemplate.opsForHash().put(key, "requirePhoneVerification", String.valueOf(settings.isRequirePhoneVerification()));
        if (settings.getIntegrationIds() != null) {
            redisTemplate.opsForHash().put(key, "integrationIds", String.join(",", settings.getIntegrationIds()));
        }
    }

    @Override
    public Optional<CampaignSettings> findByCustomerIdAndCampaignId(String customerId, String campaignId) {
        String key = buildKey(customerId, campaignId);

        //Claude: why are these objects?
        Object formSchemaId = redisTemplate.opsForHash().get(key, "formSchemaId");
        if (formSchemaId == null) {
            return Optional.empty();
        }

        Object maxSubmissions = redisTemplate.opsForHash().get(key, "maxSubmissions");
        Object allowDuplicates = redisTemplate.opsForHash().get(key, "allowDuplicateSubmissions");
        Object requireEmail = redisTemplate.opsForHash().get(key, "requireEmailVerification");
        Object requirePhone = redisTemplate.opsForHash().get(key, "requirePhoneVerification");
        Object integrationIdsStr = redisTemplate.opsForHash().get(key, "integrationIds");

        List<String> integrationIds = null;
        if (integrationIdsStr != null && !integrationIdsStr.toString().isEmpty()) {
            integrationIds = List.of(integrationIdsStr.toString().split(","));
        }

        return Optional.of(CampaignSettings.builder()
                .formSchemaId(formSchemaId.toString())
                .maxSubmissions(maxSubmissions != null ? Integer.parseInt(maxSubmissions.toString()) : null)
                .allowDuplicateSubmissions(Boolean.parseBoolean(allowDuplicates != null ? allowDuplicates.toString() : "false"))
                .requireEmailVerification(Boolean.parseBoolean(requireEmail != null ? requireEmail.toString() : "false"))
                .requirePhoneVerification(Boolean.parseBoolean(requirePhone != null ? requirePhone.toString() : "false"))
                .integrationIds(integrationIds)
                .build());
    }

    @Override
    public void delete(String customerId, String campaignId) {
        String key = buildKey(customerId, campaignId);
        redisTemplate.delete(key);
    }

    private String buildKey(String customerId, String campaignId) {
        return KEY_PREFIX + customerId + ":" + campaignId;
    }
}
