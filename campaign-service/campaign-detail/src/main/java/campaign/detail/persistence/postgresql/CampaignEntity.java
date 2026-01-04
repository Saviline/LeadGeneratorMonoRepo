package campaign.detail.persistence.postgresql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("campaigns")
public class CampaignEntity {

    @Id
    @Column("campaign_id")
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("display_name")
    private String displayName;

    private String description;

    private String status;

    @Column("formschema_id")
    private UUID formSchemaId;

    @Column("max_submissions")
    private Integer maxSubmissions;

    @Column("allow_duplicate_submissions")
    private boolean allowDuplicateSubmissions;

    @Column("require_email_verification")
    private boolean requireEmailVerification;

    @Column("require_phone_verification")
    private boolean requirePhoneVerification;

    @Column("start_date")
    private LocalDateTime startDate;

    @Column("end_date")
    private LocalDateTime endDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
