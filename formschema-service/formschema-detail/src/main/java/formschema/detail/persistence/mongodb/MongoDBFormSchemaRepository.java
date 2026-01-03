package formschema.detail.persistence.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import formschema.core.domain.FormSchema;
import formschema.core.ports.outbound.IFormSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MongoDBFormSchemaRepository implements IFormSchemaRepository<FormSchema, String> {

    private final MongoTemplate mongoTemplate;

    @Override
    public String save(FormSchema entity) {
        String id = mongoTemplate.save(entity).getId();
        log.debug("Schema has been saved in mongoDB database: schema.id={}, customer.id={}",
            id, entity.getCustomerId());
        return id;
    }

    @Override
    public Optional<FormSchema> findByIdAndCustomerId(String id, String customerId) {
        Query query = Query.query(
            Criteria.where("id").is(id).and("customerId").is(customerId)
        );
        FormSchema schema = mongoTemplate.findOne(query, FormSchema.class);
        return Optional.ofNullable(schema);
    }

    @Override
    public Boolean deleteByIdAndCustomerId(String id, String customerId) {
        Query query = Query.query(
            Criteria.where("id").is(id).and("customerId").is(customerId)
        );
        var result = mongoTemplate.remove(query, FormSchema.class);
        boolean deleted = result.getDeletedCount() > 0;
        log.debug("Schema delete attempted: schema.id={}, customer.id={}, deleted={}",
            id, customerId, deleted);
        return deleted;
    }

    @Override
    public Optional<List<FormSchema>> getAllByCustomerId(String customerId) {
        Query query = Query.query(Criteria.where("customerId").is(customerId));
        var result = mongoTemplate.find(query, FormSchema.class);
        log.debug("All Schemas retrieved: customer.id={}", customerId);
        return Optional.of(result);
    }

}