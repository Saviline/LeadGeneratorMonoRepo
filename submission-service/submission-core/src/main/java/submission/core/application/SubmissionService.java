package submission.core.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RequiredArgsConstructor
@Slf4j
public class SubmissionService {
    final private IRepositorySubmission submissionRepository;
    final private ICacheFormSchema cache;
    final private IPublish<Submission> publish;
    final private IValidate validator;
    final private IRepositoryCampaign campaignRepository;

    public Submission createSubmission(Submission submission){
        log.info("Submission is being created: submission.id={}, submission.campaignId={}, submussion.SchemaId={}",
         submission.getSubmissionId(),
          submission.getCampaignId(),
           submission.getSchemaId());

        //Is the campaign active?
        if(!isCampaignActive(submission.getCampaignId())) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setRejectionReason("Campaign not found/active");
            submissionRepository.save(submission);
            log.warn("Campaign not found/active: campaign.id={}", submission.getCampaignId());
            throw new CampaignNotActiveException(submission.getCampaignId());
        }

        log.info("CampaignId is retrieved: campaign.id={}", submission.getCampaignId());
        
        //Retrieve schema
        String schema = cache.getById(submission.getSchemaId());
        if (schema == null) {
            log.warn("Schema not found: schema.id={}", submission.getSchemaId());
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setRejectionReason("Schema not found/active");
            submissionRepository.save(submission);
            throw new SchemaNotInCacheException(submission.getSchemaId());
        }       

        log.info("Validation schema is retrieved: schema.id={}", submission.getSchemaId());

        //Validate it
       ValidationResult result = validator.validate(submission.getPayload(), schema);
        if (!result.isValid()) {
            submission.setStatus(SubmissionStatus.INVALID);
            submission.setRejectionReason(result.getErrorMessage());
            submissionRepository.save(submission);
            log.info("Submission invalid: id={}, errors={}", 
                submission.getSubmissionId(), result.getErrors());
            return submission;
        }
        log.info("Submission is validating: submissionId={}, schema.id={}", submission.getSubmissionId(), submission.getSchemaId());

        //Save it
        submission.setStatus(SubmissionStatus.VALID);
        submissionRepository.save(submission); 
    
        //Publish it
        publish.publish(submission);
        log.info("Submission is published: submissionId={}", submission.getSubmissionId());


        log.info("Submission succesfully created: submissionId={}, submission.status={}", submission.getSubmissionId(), submission.getStatus());
        return submission;
    }


    private boolean isCampaignActive(String campaignId) {
        return campaignRepository.getCampaignById(campaignId) != null;
    }

}
