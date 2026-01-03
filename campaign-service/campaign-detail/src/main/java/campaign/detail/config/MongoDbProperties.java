package campaign.detail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "mongodb")
@Validated
public record MongoDbProperties(
    Collections collections,
    Fields fields
) {
    public record Collections(
        String formSchemas
    ) {}

    public record Fields(
        String customerId,
        String schemaId
    ) {}
}
