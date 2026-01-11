package formschema.detail;

import formschema.core.domain.FormSchema;
import formschema.detail.persistence.mongodb.MongoDBFormSchemaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@Testcontainers
class MongoDbRepositoryTest {

    private static final String CUSTOMER_ID = "test-customer-123";
    private static final String OTHER_CUSTOMER_ID = "other-customer-456";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    private MongoDBFormSchemaRepository repository;
    private MongoTemplate mongoTemplate;
    private MongoClient mongoClient;

    @BeforeEach
    void setUp() {
        String connectionString = mongoDBContainer.getReplicaSetUrl();
        mongoClient = MongoClients.create(connectionString);
        mongoTemplate = new MongoTemplate(mongoClient, "test_db");
        mongoTemplate.dropCollection(FormSchema.class);
        repository = new MongoDBFormSchemaRepository(mongoTemplate);
    }

    @AfterEach
    void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    void save_ShouldPersistSchema() {
        FormSchema schema = new FormSchema();
        schema.setName("Test Schema");
        schema.setCustomerId(CUSTOMER_ID);

        String id = repository.save(schema);

        Optional<FormSchema> found = repository.findByIdAndCustomerId(id, CUSTOMER_ID);

        assertTrue(found.isPresent(), "Schema should be found");
        assertEquals(id, found.get().getId());
        assertEquals(CUSTOMER_ID, found.get().getCustomerId());
        assertEquals("Test Schema", found.get().getName());
    }

    @Test
    void findByIdAndCustomerId_NonExistent_ShouldReturnEmpty() {
        Optional<FormSchema> found = repository.findByIdAndCustomerId("non-existent-id", CUSTOMER_ID);

        assertTrue(found.isEmpty(), "Should return empty for non-existent schema");
    }

    @Test
    void findByIdAndCustomerId_WrongCustomer_ShouldReturnEmpty() {
        FormSchema schema = new FormSchema();
        schema.setName("Test Schema");
        schema.setCustomerId(CUSTOMER_ID);

        String id = repository.save(schema);

        Optional<FormSchema> found = repository.findByIdAndCustomerId(id, OTHER_CUSTOMER_ID);

        assertTrue(found.isEmpty(), "Should not find schema for wrong customer");
    }

    @Test
    void deleteByIdAndCustomerId_ShouldRemoveSchema() {
        FormSchema schema = new FormSchema();
        schema.setName("To Delete");
        schema.setCustomerId(CUSTOMER_ID);

        String id = repository.save(schema);

        Boolean deleted = repository.deleteByIdAndCustomerId(id, CUSTOMER_ID);

        assertTrue(deleted, "Delete should return true");
        Optional<FormSchema> found = repository.findByIdAndCustomerId(id, CUSTOMER_ID);
        assertTrue(found.isEmpty(), "Schema should be deleted");
    }

    @Test
    void deleteByIdAndCustomerId_WrongCustomer_ShouldNotDelete() {
        FormSchema schema = new FormSchema();
        schema.setName("Protected Schema");
        schema.setCustomerId(CUSTOMER_ID);

        String id = repository.save(schema);

        Boolean deleted = repository.deleteByIdAndCustomerId(id, OTHER_CUSTOMER_ID);

        assertFalse(deleted, "Delete should return false for wrong customer");
        Optional<FormSchema> found = repository.findByIdAndCustomerId(id, CUSTOMER_ID);
        assertTrue(found.isPresent(), "Schema should still exist");
    }

    @Test
    void getAllByCustomerId_ShouldReturnOnlyCustomerSchemas() {
        FormSchema schema1 = new FormSchema();
        schema1.setName("Schema 1");
        schema1.setCustomerId(CUSTOMER_ID);

        FormSchema schema2 = new FormSchema();
        schema2.setName("Schema 2");
        schema2.setCustomerId(CUSTOMER_ID);

        FormSchema otherSchema = new FormSchema();
        otherSchema.setName("Other Schema");
        otherSchema.setCustomerId(OTHER_CUSTOMER_ID);

        repository.save(schema1);
        repository.save(schema2);
        repository.save(otherSchema);

        Optional<List<FormSchema>> result = repository.getAllByCustomerId(CUSTOMER_ID);

        assertTrue(result.isPresent(), "Should return schemas");
        assertEquals(2, result.get().size(), "Should return only schemas for the customer");
    }
}
