package submission.core.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CampaignSettings {
    private String formSchemaId;
    private List<String> integrationIds;
    private Integer maxSubmissions;
    private boolean allowDuplicateSubmissions;
    private boolean requireEmailVerification;
    private boolean requirePhoneVerification;
}
