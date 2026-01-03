package submission.core.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Campaign {
    private String campaignId;
    private String customerId;
    private String formSchemaId;
}
