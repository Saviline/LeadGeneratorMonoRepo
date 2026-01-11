package submission.core.service;

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
import submission.core.exception.CampaignNotActiveException;
import submission.core.exception.SchemaNotInCacheException;
import submission.core.ports.ICampaignSettingsRepository;
import submission.core.ports.IFormSchemaRepository;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {

    private static final String CUSTOMER_ID = "customer-1";
    private static final String CAMPAIGN_ID = "campaign-1";
    private static final String SCHEMA_ID = "schema-1";
    private static final String VALID_SCHEMA = "{}";

    @Mock private IRepositorySubmission submissionRepository;
    @Mock private IFormSchemaRepository formSchemaRepository;
    @Mock private IPublish<Submission> publisher;
    @Mock private IValidate validator;
    @Mock private ICampaignSettingsRepository campaignSettingsRepository;

    private SubmissionService service;

    @BeforeEach
    void setUp() {
        service = new SubmissionService(submissionRepository, formSchemaRepository,
                publisher, validator, campaignSettingsRepository);
    }

    @Test
    void createSubmission_AllValid_ShouldReturnValidAndPublish() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .maxSubmissions(100)
                .allowDuplicateSubmissions(false)
                .build();

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.of(VALID_SCHEMA));
        when(validator.validate(any(), any())).thenReturn(ValidationResult.success());

        Submission submission = buildSubmission(CAMPAIGN_ID);

        Submission result = service.createSubmission(submission, CUSTOMER_ID);

        assertEquals(SubmissionStatus.VALID, result.getStatus());
        assertNull(result.getRejectionReason());

        verify(submissionRepository).save(submission);
        verify(publisher).publish(submission);
    }

    @Test
    void createSubmission_CampaignNotFound_ShouldRejectAndThrow() {
        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.empty());

        Submission submission = buildSubmission(CAMPAIGN_ID);

        assertThrows(CampaignNotActiveException.class, () -> {
            service.createSubmission(submission, CUSTOMER_ID);
        });

        assertEquals(SubmissionStatus.REJECTED, submission.getStatus());
        assertEquals("Campaign not found or not active", submission.getRejectionReason());

        verify(submissionRepository).save(submission);
        verify(publisher, never()).publish(any());
    }

    @Test
    void createSubmission_SchemaNotFound_ShouldRejectAndThrow() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .build();

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.empty());

        Submission submission = buildSubmission(CAMPAIGN_ID);

        assertThrows(SchemaNotInCacheException.class, () -> {
            service.createSubmission(submission, CUSTOMER_ID);
        });

        assertEquals(SubmissionStatus.REJECTED, submission.getStatus());
        assertEquals("Schema not found", submission.getRejectionReason());

        verify(submissionRepository).save(submission);
        verify(publisher, never()).publish(any());
    }

    @Test
    void createSubmission_ValidationFails_ShouldReturnInvalid() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .build();

        String errorMessage = "Email field is required";

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.of(VALID_SCHEMA));
        when(validator.validate(any(), any())).thenReturn(ValidationResult.failure(errorMessage));

        Submission submission = buildSubmission(CAMPAIGN_ID);

        Submission result = service.createSubmission(submission, CUSTOMER_ID);

        assertEquals(SubmissionStatus.INVALID, result.getStatus());
        assertEquals(errorMessage, result.getRejectionReason());

        verify(submissionRepository).save(submission);
    }

    @Test
    void createSubmission_ValidationFails_ShouldNotPublish() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .build();

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.of(VALID_SCHEMA));
        when(validator.validate(any(), any())).thenReturn(ValidationResult.failure("Invalid data"));

        Submission submission = buildSubmission(CAMPAIGN_ID);

        service.createSubmission(submission, CUSTOMER_ID);

        verify(publisher, never()).publish(any());
    }

    @Test
    void createSubmission_Valid_ShouldSaveToRepository() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .build();

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.of(VALID_SCHEMA));
        when(validator.validate(any(), any())).thenReturn(ValidationResult.success());

        Submission submission = buildSubmission(CAMPAIGN_ID);

        service.createSubmission(submission, CUSTOMER_ID);

        verify(submissionRepository).save(submission);
    }

    @Test
    void createSubmission_Rejected_ShouldSaveWithRejectionReason() {
        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.empty());

        Submission submission = buildSubmission(CAMPAIGN_ID);

        try {
            service.createSubmission(submission, CUSTOMER_ID);
        } catch (CampaignNotActiveException ignored) {
        }

        verify(submissionRepository).save(argThat(saved ->
                saved.getStatus() == SubmissionStatus.REJECTED &&
                        saved.getRejectionReason() != null
        ));
    }

    @Test
    void createSubmission_Invalid_ShouldReturnSubmissionWithStatus() {
        CampaignSettings settings = CampaignSettings.builder()
                .formSchemaId(SCHEMA_ID)
                .build();

        when(campaignSettingsRepository.findByCustomerIdAndCampaignId(CUSTOMER_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(settings));
        when(formSchemaRepository.findByCustomerIdAndSchemaId(CUSTOMER_ID, SCHEMA_ID))
                .thenReturn(Optional.of(VALID_SCHEMA));
        when(validator.validate(any(), any())).thenReturn(ValidationResult.failure("Field missing"));

        Submission submission = buildSubmission(CAMPAIGN_ID);

        Submission result = service.createSubmission(submission, CUSTOMER_ID);

        assertNotNull(result);
        assertEquals(SubmissionStatus.INVALID, result.getStatus());
        assertNotNull(result.getRejectionReason());
    }

    private Submission buildSubmission(String campaignId) {
        return Submission.builder()
                .submissionId(UUID.randomUUID().toString())
                .campaignId(campaignId)
                .payload(Map.of("email", "test@test.com"))
                .build();
    }
}
