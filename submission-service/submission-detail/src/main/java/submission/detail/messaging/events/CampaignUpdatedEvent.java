package submission.detail.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampaignUpdatedEvent extends EventBase {

    private String campaignId;
    private String displayName;
    private String description;
    private String formSchemaId;
    private String status;
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
