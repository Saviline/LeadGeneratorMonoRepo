import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import submission.core.application.SubmissionService;
import submission.core.domain.Submission;
import submission.core.domain.SubmissionStatus;
import submission.core.domain.ValidationResult;
import submission.core.ports.ICacheFormSchema;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositoryCampaign;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {
    @Mock private IRepositorySubmission submissionRepository;
    @Mock private ICacheFormSchema cache;
    @Mock private IPublish<Submission> publisher;
    @Mock private IValidate validator;
    @Mock private IRepositoryCampaign campaignRepository;

    private SubmissionService service;

    @BeforeEach
    public void setup(){
        service = new SubmissionService(submissionRepository, cache, publisher, validator, campaignRepository);
    }

    @Test
    void createSubmission_AllValid_ReturnsValidAndPublishes() {
        // Arrange
        when(campaignRepository.getCampaignById("camp-1")).thenReturn("camp-1");
        when(cache.getById("schema-1")).thenReturn("{}");
        when(validator.validate(any(), any())).thenReturn(ValidationResult.success());

        Submission submission = buildSubmission("camp-1", "schema-1");

        // Act
        Submission result = service.createSubmission(submission);

        assertEquals(result.getStatus(), SubmissionStatus.VALID);
        
        verify(submissionRepository).save(submission);
        
        verify(publisher).publish(submission);
    }


     private Submission buildSubmission(String campaignId, String schemaId) {
        return Submission.builder()
            .submissionId(UUID.randomUUID().toString())
            .campaignId(campaignId)
            .schemaId(schemaId)
            .payload(Map.of("email", "test@test.com"))
            .build();
    }
}