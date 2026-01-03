package campaign.core.ports.outbound;

import campaign.core.domain.FormSchema;
import reactor.core.publisher.Mono;

public interface IFormSchemaRepository {
    
    Mono<FormSchema> saveFormSchema(FormSchema schema, String customerId);
    Mono<FormSchema> getFormSchema(String schema, String customerId);
    Mono<Boolean> schemaExists(String schema, String customerId);

}
