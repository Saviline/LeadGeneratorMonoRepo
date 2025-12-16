package submission.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import submission.detail.FormSchemaListener;
import submission.detail.ReditRepository;
import submission.detail.JsonSchemaValidator;

@Configuration
public class ApplicationConfig {

    @Bean
    public JsonSchemaValidator validatorService() {
        return new JsonSchemaValidator(new ObjectMapper());
    }

    @Bean
    public ReditRepository RedisRepository() {
        return new ReditRepository();
    }

    @Bean
    public FormSchemaListener formSchemaListener(ReditRepository repository) {
        return new FormSchemaListener(repository);
    }
}
