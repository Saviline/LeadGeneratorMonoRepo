package submission.core.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final IRepositorySubmission submissionRepository;
    private final IFormSchemaRepository formSchemaRepository;
    private final IPublish<Submission> publish;
    private final IValidate validator;
    private final ICampaignSettingsRepository campaignSettingsRepository;

    public Submission createSubmission(Submission submission, String customerId) {
        log.info("Processing submission: submission.id={}, campaign.id={}, customer.id={}",
            submission.getSubmissionId(),
            submission.getCampaignId(),
            customerId);

        Optional<CampaignSettings> settingsOpt = campaignSettingsRepository.findByCustomerIdAndCampaignId(
            customerId, submission.getCampaignId());

        if (settingsOpt.isEmpty()) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setRejectionReason("Campaign not found or not active");
            submissionRepository.save(submission);
            log.warn("Submission rejected. Campaign not found for customer: submission.id={}, campaign.id={}, customer.id={}",
                submission.getSubmissionId(), submission.getCampaignId(), customerId);
            throw new CampaignNotActiveException(submission.getCampaignId());
        }

        CampaignSettings settings = settingsOpt.get();
        String formSchemaId = settings.getFormSchemaId();
        log.debug("Campaign verified: campaign.id={}, formSchema.id={}", submission.getCampaignId(), formSchemaId);

        Optional<String> schemaOpt = formSchemaRepository.findByCustomerIdAndSchemaId(customerId, formSchemaId);
        if (schemaOpt.isEmpty()) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setRejectionReason("Schema not found");
            submissionRepository.save(submission);
            log.warn("Submission rejected. Schema not found: submission.id={}, schema.id={}",
                submission.getSubmissionId(), formSchemaId);
            throw new SchemaNotInCacheException(formSchemaId);
        }

        String schema = schemaOpt.get();
        log.debug("Schema retrieved: schema.id={}", formSchemaId);

        ValidationResult result = validator.validate(submission.getPayload(), schema);
        if (!result.isValid()) {
            submission.setStatus(SubmissionStatus.INVALID);
            submission.setRejectionReason(result.getErrorMessage());
            submissionRepository.save(submission);
            log.info("Submission invalid: submission.id={}, reason={}",
                submission.getSubmissionId(), result.getErrorMessage());
            return submission;
        }
        log.debug("Validation passed: submission.id={}", submission.getSubmissionId());

        submission.setStatus(SubmissionStatus.VALID);
        submissionRepository.save(submission);
        log.debug("Submission saved: submission.id={}", submission.getSubmissionId());

        publish.publish(submission);
        log.debug("Submission published: submission.id={}", submission.getSubmissionId());

        log.info("Submission created successfully: submission.id={}, status={}",
            submission.getSubmissionId(), submission.getStatus());
        return submission;
    }
}
