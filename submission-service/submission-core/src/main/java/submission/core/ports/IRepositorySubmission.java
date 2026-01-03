package submission.core.ports;

import submission.core.domain.Submission;

public interface IRepositorySubmission {
    void save(Submission submission);
    Submission getById(String id);
}
