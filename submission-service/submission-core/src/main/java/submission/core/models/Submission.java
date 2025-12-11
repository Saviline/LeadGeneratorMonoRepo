package submission.core.models;
import java.time.Instant;
import java.util.Map;

import lombok.Data;

@Data
public class Submission {

    private String id; 

    private String campaignId;

    private String schemaId; 

    private String rewardId;

    private Instant receivedAt = Instant.now();

    private Map<String, Object> payload;
    
    private String status; // e.g., "NEW", "PROCESSED", "FAILED"
    
}
