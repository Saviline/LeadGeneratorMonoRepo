package submission.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import submission.core.ports.IFormSchemaRepository;

import java.util.Optional;

@RequiredArgsConstructor
public class RedisFormSchemaRepository implements IFormSchemaRepository {

    private static final String KEY_PREFIX = "formSchema:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String customerId, String schemaId, String validationSchema) {
        String key = buildKey(customerId, schemaId);
        redisTemplate.opsForValue().set(key, validationSchema);
    }

    @Override
    public Optional<String> findByCustomerIdAndSchemaId(String customerId, String schemaId) {
        String key = buildKey(customerId, schemaId);
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    private String buildKey(String customerId, String schemaId) {
        return KEY_PREFIX + customerId + ":" + schemaId;
    }
}
