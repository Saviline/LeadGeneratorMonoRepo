package submission.detail.mongodb;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.RequiredArgsConstructor;
import submission.core.domain.Submission;
import submission.core.ports.IRepositorySubmission;

@RequiredArgsConstructor
public class MongoDBSubmissionRepository implements IRepositorySubmission {

     private final MongoTemplate mongoTemplate;

     @Override
     public void save(Submission submission) {
        mongoTemplate.save(submission);
     }

     @Override
     public Submission getById(String id, String customerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("submissionId").is(id).and("customerId").is(customerId));
        return mongoTemplate.findOne(query, Submission.class);
     }
}
