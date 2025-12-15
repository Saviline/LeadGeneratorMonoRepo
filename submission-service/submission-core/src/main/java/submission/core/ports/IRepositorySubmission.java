package submission.core.ports;

import submission.core.domain.Submission;

public interface IRepositorySubmission {
    public String save(Submission submission);
    public Submission getById(String id);
}
