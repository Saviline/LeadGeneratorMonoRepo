package submission.detail;

import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;
import submission.core.ports.ICacheFormSchema;

@RequiredArgsConstructor
public class RedisFormSchemaCache implements ICacheFormSchema {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String formSchemaId, String validationSchema) {
        redisTemplate.opsForValue().set(formSchemaId, validationSchema);
    }

    @Override
    public String getById(String id) {
        return redisTemplate.opsForValue().get(id);
    }
}
