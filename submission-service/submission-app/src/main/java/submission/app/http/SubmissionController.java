package submission.app.http;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.app.http.dto.SubmissionRequest;
import submission.app.http.dto.SubmissionResponse;
import submission.core.application.SubmissionService;
import submission.core.domain.Submission;
import submission.core.domain.SubmissionStatus;

@Slf4j
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(
            @RequestBody SubmissionRequest request,
            @RequestHeader("X-Customer-ID") String customerId) {

        log.debug("Received submission request: campaignId={}, schemaId={}, customerId={}",
            request.getCampaignId(), request.getSchemaId(), customerId);

        Submission submission = Submission.builder()
            .submissionId(UUID.randomUUID().toString())
            .campaignId(request.getCampaignId())
            .schemaId(request.getSchemaId())
            .payload(request.getPayload())
            .build();

        Submission result = submissionService.createSubmission(submission, customerId);

        HttpStatus httpStatus = result.getStatus() == SubmissionStatus.VALID
            ? HttpStatus.CREATED
            : HttpStatus.UNPROCESSABLE_ENTITY;
                //It needs to send back all reasons for rejection, so they can fix it, which is not sensitive or secuirty breach for us. 
        return ResponseEntity.status(httpStatus).body(SubmissionResponse.builder()
            .submissionId(result.getSubmissionId())
            .status(result.getStatus().name())
            .reason(result.getRejectionReason())
            .build());
    }
}
