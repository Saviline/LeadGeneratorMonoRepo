package submission.core.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.core.domain.Campaign;
import submission.core.domain.Submission;
import submission.core.domain.SubmissionStatus;
import submission.core.domain.ValidationResult;
import submission.core.exception.CampaignNotActiveException;
import submission.core.exception.SchemaNotInCacheException;
import submission.core.ports.ICacheFormSchema;
import submission.core.ports.IPublish;
import submission.core.ports.IRepositoryCampaign;
import submission.core.ports.IRepositorySubmission;
import submission.core.ports.IValidate;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class SubmissionService {
    private final IRepositorySubmission submissionRepository;
    private final ICacheFormSchema cache;
    private final IPublish<Submission> publish;
    private final IValidate validator;
    private final IRepositoryCampaign campaignRepository;

    public Submission createSubmission(Submission submission, String customerId) {
        log.info("Processing submission: submission.id={}, campaign.id={}, schema.id={}, customer.id={}",
            submission.getSubmissionId(),
            submission.getCampaignId(),
            submission.getSchemaId(),
            customerId);

        // Is the campaign active and owned by this customer?
        Optional<Campaign> campaignOpt = campaignRepository.findByIdAndCustomerId(
            submission.getCampaignId(), customerId);

        if (campaignOpt.isEmpty()) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setRejectionReason("Campaign not found or not owned by customer");
            submissionRepository.save(submission);
            log.warn("Submission rejected. Campaign not found for customer: submission.id={}, campaign.id={}, customer.id={}",
                submission.getSubmissionId(), submission.getCampaignId(), customerId);
            throw new CampaignNotActiveException(submission.getCampaignId());
        }

        Campaign campaign = campaignOpt.get();
        log.debug("Campaign verified: campaign.id={}, customer.id={}", campaign.getCampaignId(), customerId);

        // Retrieve schema from cache
        String schema = cache.getById(submission.getSchemaId());
        if (schema == null) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setRejectionReason("Schema not found in cache");
            submissionRepository.save(submission);
            log.warn("Submission rejected. Schema not found: submission.id={}, schema.id={}",
                submission.getSubmissionId(), submission.getSchemaId());
            throw new SchemaNotInCacheException(submission.getSchemaId());
        }
        log.debug("Schema retrieved from cache: schema.id={}", submission.getSchemaId());

        // Validate payload against schema
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

        // Save valid submission
        submission.setStatus(SubmissionStatus.VALID);
        submissionRepository.save(submission);
        log.debug("Submission saved: submission.id={}", submission.getSubmissionId());

        // Publish to lead service
        publish.publish(submission);
        log.debug("Submission published: submission.id={}", submission.getSubmissionId());

        log.info("Submission created successfully: submission.id={}, status={}",
            submission.getSubmissionId(), submission.getStatus());
        return submission;
    }
}
