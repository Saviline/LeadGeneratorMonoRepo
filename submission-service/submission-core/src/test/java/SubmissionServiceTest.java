import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import submission.core.application.SubmissionService;
import submission.core.domain.CampaignSettings;
import submission.core.domain.Submission;
import submission.core.domain.SubmissionStatus;
import submission.core.domain.ValidationResult;
import submission.core.ports.ICampaignSettingsRepository;
import submission.core.ports.IFormSchemaRepository;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {
    @Mock private IRepositorySubmission submissionRepository;
    @Mock private IFormSchemaRepository formSchemaRepository;
    @Mock private IPublish<Submission> publisher;
    @Mock private IValidate validator;
    @Mock private ICampaignSettingsRepository campaignSettingsRepository;

    private SubmissionService service;

    private static final String CUSTOMER_ID = "customer-1";
    private static final String CAMPAIGN_ID = "camp-1";
    private static final String SCHEMA_ID = "schema-1";

    @BeforeEach
    public void setup(){
        service = new SubmissionService(submissionRepository, formSchemaRepository, publisher, validator, campaignSettingsRepository);
    }

    @Test
    void createSubmission_AllValid_ReturnsValidAndPublishes() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .maxSubmissions(100)
                .allowDuplicateSubmissions(false)
                .build();

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.of("{}"));
        when(validator.validate(any(), any())).thenReturn(ValidationResult.success());

        Submission submission = buildSubmission(CAMPAIGN_ID);

        Submission result = service.createSubmission(submission, CUSTOMER_ID);

        assertEquals(SubmissionStatus.VALID, result.getStatus());

        verify(submissionRepository).save(submission);
        verify(publisher).publish(submission);
    }

    private Submission buildSubmission(String campaignId) {
        return Submission.builder()
            .submissionId(UUID.randomUUID().toString())
            .campaignId(campaignId)
            .payload(Map.of("email", "test@test.com"))
            .build();
    }
}
