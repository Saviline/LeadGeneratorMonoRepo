package submission.app.http.dto;

import java.util.Map;

import lombok.Data;

@Data
public class SubmissionRequest {
    private String campaignId;
    private Map<String, Object> payload;
}
