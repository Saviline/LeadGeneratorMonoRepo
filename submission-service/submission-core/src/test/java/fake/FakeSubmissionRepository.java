package fake;
import java.util.HashMap;
import java.util.Map;

import submission.core.domain.Submission;
import submission.core.ports.IRepositorySubmission;

public class FakeSubmissionRepository implements IRepositorySubmission{

    Map<String, Submission> repository = new HashMap<>();

    @Override
    public void save(Submission submission) {
        repository.put(submission.getSubmissionId(), submission);
    }

    @Override
    public Submission getById(String id) {
        return repository.get(id);
    }
    
}
