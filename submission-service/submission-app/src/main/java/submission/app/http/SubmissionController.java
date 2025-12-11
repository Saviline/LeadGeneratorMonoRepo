package submission.app.http;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class SubmissionController {

    @PostMapping("/submit")
    public void sendSubmission(String message) {
        //Should receive message from client and process it
    }
}
