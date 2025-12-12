package submission.app.http;


import java.util.Map;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import submission.detail.ReditRepository;
import submission.detail.ValidatorService;
import submission.detail.dto.ValidationException;

@RequiredArgsConstructor
@RestController
public class SubmissionController {

    final private ValidatorService validatorService;
    final private ReditRepository repository;
    Logger logger = org.slf4j.LoggerFactory.getLogger(SubmissionController.class);

    @PostMapping("/submit")
    public void sendSubmission(@RequestBody Map<String, Object> submissionData) {
        
        //Retrieve the form schema ID
        if(submissionData.isEmpty())
            throw new IllegalArgumentException("Submission data cannot be empty");

        String schemaName = submissionData.get("name").toString();
        logger.info("Received submission for schema: " + schemaName);

        //Retrieve the form schema from the repository
        var schema = repository.getFormSchemaByName(schemaName);
        logger.info("Retrieved schema: " + schema.getName());

        try {
        	validatorService.validate(submissionData, schema);
        } catch (ValidationException e) {
        	e.printStackTrace();
        }
        
        //Save submission in database and send to lead for futher processing
    }
}
