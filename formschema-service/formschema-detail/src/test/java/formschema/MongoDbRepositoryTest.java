package formschema;
import formschema.core.models.FormSchema;
import formschema.detail.mongodb.MongoDBFormSchemaRepository;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

@Testcontainers
class MongoDbRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    private MongoDBFormSchemaRepository repository;

    private MongoTemplate mongoTemplate;

    private MongoClient mongoClient;

    

    @BeforeEach // Use JUnit 5's annotation, not void setUp() without annotation
    void setUp() {
        // 1. Get the random port/connection string from the running container
        String connectionString = mongoDBContainer.getReplicaSetUrl();

        // 2. Create a Native Mongo Client manually (No Spring DI)
        mongoClient = MongoClients.create(connectionString);

        // 3. Create the MongoTemplate manually
        // We pass the client and the database name we want to use ("test_db")
        mongoTemplate = new MongoTemplate(mongoClient, "test_db");

        // 4. Inject it into your repository
        repository = new MongoDBFormSchemaRepository(mongoTemplate);
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (this.mongoClient != null) {
            this.mongoClient.close(); // <--- This kills the background threads
        }
    }

    @Test
    void should_save_schema() 
    {
        //Arrange
        FormSchema schema = new FormSchema();
        schema.setName("Test Schema");

        //Act
        String id = repository.save(schema);

        //Assert
        FormSchema found = repository.findFormSchemaById(id);

        assertEquals(found.getId(), id);
    }
}
