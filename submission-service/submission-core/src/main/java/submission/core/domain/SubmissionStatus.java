package submission.core.domain;

public enum SubmissionStatus {
    PENDING,           // Just received
    REJECTED,          // Campaign not active
    INVALID,           // Validation failed
    VALID   
}