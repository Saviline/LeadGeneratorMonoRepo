package submission.core.ports;

import submission.core.domain.Submission;

public interface IRepositorySubmission {
    void save(Submission submission);  // ID already set, just save
    Submission getById(String id);
}
