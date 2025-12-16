package submission.app.http.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponse {
    private String submissionId;
    private String status;
    private String reason;  // null if valid, error message if rejected/invalid
}
