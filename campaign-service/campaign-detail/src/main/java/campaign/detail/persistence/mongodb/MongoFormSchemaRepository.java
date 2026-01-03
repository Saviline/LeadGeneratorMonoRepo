package campaign.detail.persistence.mongodb;

import campaign.core.domain.FormSchema;
import campaign.core.ports.outbound.IFormSchemaRepository;
import campaign.detail.config.MongoDbProperties;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.text.Normalizer.Form;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Criteria;

@Repository
@RequiredArgsConstructor
public class MongoFormSchemaRepository implements IFormSchemaRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MongoDbProperties mongoDbProperties;

    @Override
    public Mono<Boolean> schemaExists(String schema, String customerId) {
        Query query = new Query()
            .addCriteria(Criteria.where(mongoDbProperties.fields().customerId()).is(customerId))
            .addCriteria(Criteria.where(mongoDbProperties.fields().schemaId()).is(schema));
        return reactiveMongoTemplate.exists(query, mongoDbProperties.collections().formSchemas());
    }

    @Override
    public Mono<FormSchema> saveFormSchema(FormSchema schema, String customerId) {
        schema.setCustomerId(customerId);
        return reactiveMongoTemplate.save(schema, mongoDbProperties.collections().formSchemas());
    }

    @Override
    public Mono<FormSchema> getFormSchema(String schema, String customerId) {
        Query query = new Query()
            .addCriteria(Criteria.where(mongoDbProperties.fields().customerId()).is(customerId))
            .addCriteria(Criteria.where(mongoDbProperties.fields().schemaId()).is(schema));
        return reactiveMongoTemplate.findOne(query, FormSchema.class, mongoDbProperties.collections().formSchemas());
    }
}
