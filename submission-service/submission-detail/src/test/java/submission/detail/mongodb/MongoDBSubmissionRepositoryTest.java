package submission.detail.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import submission.core.domain.Submission;
import submission.core.domain.SubmissionStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MongoDBSubmissionRepositoryTest {

    private static final String CUSTOMER_ID = "customer-123";
    private static final String OTHER_CUSTOMER_ID = "other-customer-456";
    private static final String CAMPAIGN_ID = "campaign-789";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    private MongoDBSubmissionRepository repository;
    private MongoTemplate mongoTemplate;
    private MongoClient mongoClient;

    @BeforeEach
    void setUp() {
        String connectionString = mongoDBContainer.getReplicaSetUrl();
        mongoClient = MongoClients.create(connectionString);
        mongoTemplate = new MongoTemplate(mongoClient, "test_db");
        mongoTemplate.dropCollection("submission");
        repository = new MongoDBSubmissionRepository(mongoTemplate);
    }

    @AfterEach
    void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    void save_ShouldPersistSubmission() {
        Submission submission = buildSubmission(CUSTOMER_ID);

        repository.save(submission);

        Query query = new Query(Criteria.where("submissionId").is(submission.getSubmissionId()));
        Document found = mongoTemplate.findOne(query, Document.class, "submission");

        assertNotNull(found);
        assertEquals(submission.getSubmissionId(), found.getString("submissionId"));
        assertEquals(CUSTOMER_ID, found.getString("customerId"));
        assertEquals(CAMPAIGN_ID, found.getString("campaignId"));
    }

    @Test
    void getById_ExistingSubmission_ShouldReturnDocument() {
        Submission submission = buildSubmission(CUSTOMER_ID);
        submission.setStatus(SubmissionStatus.VALID);

        mongoTemplate.save(submission, "submission");

        Query query = new Query(Criteria.where("submissionId").is(submission.getSubmissionId())
                .and("customerId").is(CUSTOMER_ID));
        Document found = mongoTemplate.findOne(query, Document.class, "submission");

        assertNotNull(found);
        assertEquals("VALID", found.getString("status"));
    }

    @Test
    void getById_WrongCustomer_ShouldReturnNull() {
        Submission submission = buildSubmission(CUSTOMER_ID);

        mongoTemplate.save(submission, "submission");

        Query query = new Query(Criteria.where("submissionId").is(submission.getSubmissionId())
                .and("customerId").is(OTHER_CUSTOMER_ID));
        Document found = mongoTemplate.findOne(query, Document.class, "submission");

        assertNull(found);
    }

    @Test
    void getById_NonExistent_ShouldReturnNull() {
        Query query = new Query(Criteria.where("submissionId").is("non-existent-id")
                .and("customerId").is(CUSTOMER_ID));
        Document found = mongoTemplate.findOne(query, Document.class, "submission");

        assertNull(found);
    }

    @Test
    void save_WithRejectionReason_ShouldPersist() {
        Submission submission = buildSubmission(CUSTOMER_ID);
        submission.setStatus(SubmissionStatus.REJECTED);
        submission.setRejectionReason("Campaign not active");

        repository.save(submission);

        Query query = new Query(Criteria.where("submissionId").is(submission.getSubmissionId()));
        Document found = mongoTemplate.findOne(query, Document.class, "submission");

        assertNotNull(found);
        assertEquals("REJECTED", found.getString("status"));
        assertEquals("Campaign not active", found.getString("rejectionReason"));
    }

    private Submission buildSubmission(String customerId) {
        return Submission.builder()
                .submissionId(UUID.randomUUID().toString())
                .customerId(customerId)
                .campaignId(CAMPAIGN_ID)
                .receivedAt(Instant.now())
                .payload(Map.of("email", "test@test.com", "name", "Test User"))
                .status(SubmissionStatus.PENDING)
                .build();
    }
}
