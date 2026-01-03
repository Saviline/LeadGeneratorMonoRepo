package submission.core.domain;
import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Submission {

    private String submissionId;
    private String customerId;
    private String campaignId;
    private String schemaId; 

    @Builder.Default
    private Instant receivedAt = Instant.now();

    private Map<String, Object> payload;

    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    private String rejectionReason;
}


