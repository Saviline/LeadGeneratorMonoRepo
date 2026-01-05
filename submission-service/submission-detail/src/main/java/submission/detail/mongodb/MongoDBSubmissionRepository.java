package submission.detail.mongodb;

import org.springframework.data.mongodb.core.MongoTemplate;

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
     public Submission getById(String id) {
        return mongoTemplate.findById(id, Submission.class);
     }
}
