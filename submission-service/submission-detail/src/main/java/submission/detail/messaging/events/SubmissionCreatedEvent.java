package submission.detail.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import submission.core.domain.Submission;
import submission.core.domain.SubmissionStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionCreatedEvent extends EventBase {

    private String submissionId;
    private String campaignId;
    private String schemaId;
    private Instant receivedAt;
    private Map<String, Object> formData;
    private SubmissionStatus status;
    private String rejectionReason;

    public static SubmissionCreatedEvent from(Submission submission) {
        return SubmissionCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SUBMISSION_CREATED")
                .timestamp(Instant.now())
                .source("submission-service")
                .customerId(submission.getCustomerId())
                .submissionId(submission.getSubmissionId())
                .campaignId(submission.getCampaignId())
                .schemaId(submission.getSchemaId())
                .receivedAt(submission.getReceivedAt())
                .formData(submission.getPayload())
                .status(submission.getStatus())
                .rejectionReason(submission.getRejectionReason())
                .build();
    }
}
