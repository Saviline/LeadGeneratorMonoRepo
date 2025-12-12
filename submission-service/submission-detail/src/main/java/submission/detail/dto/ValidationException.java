package submission.detail.dto;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final String field;
    private final String clientMessage;

    public ValidationException(String field, String clientMessage) {
        // 2. Pass a log-friendly message to the parent RuntimeException
        super(String.format("Validation failed for field '%s': %s", field, clientMessage));
        this.field = field;
        this.clientMessage = clientMessage;
    }
}