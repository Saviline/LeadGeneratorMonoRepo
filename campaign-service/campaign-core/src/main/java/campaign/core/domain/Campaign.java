package campaign.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    private String id;
    private String customerId;
    private String displayName;
    private String description;
    private CampaignStatus status;
    private String formSchemaId;
    private Integer maxSubmissions;
    private boolean allowDuplicateSubmissions;
    private boolean requireEmailVerification;
    private boolean requirePhoneVerification;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> integrationIds;
}
