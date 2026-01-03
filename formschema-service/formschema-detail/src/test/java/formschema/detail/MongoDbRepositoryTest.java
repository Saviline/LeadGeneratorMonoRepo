package formschema.detail;
import formschema.core.domain.FormSchema;
import formschema.detail.persistence.mongodb.MongoDBFormSchemaRepository;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

@Testcontainers
class MongoDbRepositoryTest {

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
        repository = new MongoDBFormSchemaRepository(mongoTemplate);
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    @Test
    void should_save_schema()
    {
        String customerId = "money";
        FormSchema schema = new FormSchema();
        schema.setName("Test Schema");

        String id = repository.save(schema);

        Optional<FormSchema> found = repository.findByIdAndCustomerId(id, customerId);

        assertEquals(found.get().getId(), id);
    }
}
