package submission.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import submission.detail.FormSchemaListener;
import submission.detail.ReditRepository;
import submission.detail.ValidatorService;

@Configuration
public class ApplicationConfig {

    @Bean
    public ValidatorService validatorService() {
        return new ValidatorService();
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
