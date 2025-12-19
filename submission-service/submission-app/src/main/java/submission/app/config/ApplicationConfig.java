package submission.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import submission.detail.FormSchemaListener;
import submission.detail.RedisFormSchemaCache;
import submission.detail.JsonSchemaValidator;

@Configuration
public class ApplicationConfig {

    @Bean
    public JsonSchemaValidator validatorService() {
        return new JsonSchemaValidator(new ObjectMapper());
    }

    @Bean
    public RedisFormSchemaCache RedisRepository(RedisTemplate<String, String> redisTemplate) {
        return new RedisFormSchemaCache(redisTemplate);
    }

    @Bean
    public FormSchemaListener formSchemaListener(RedisFormSchemaCache repository) {
        return new FormSchemaListener(repository);
    }
}
