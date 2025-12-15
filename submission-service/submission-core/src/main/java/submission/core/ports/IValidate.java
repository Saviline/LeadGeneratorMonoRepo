package submission.core.ports;

import java.util.Map;

import submission.core.domain.ValidationResult;

public interface IValidate {
    public ValidationResult validate(Map<String, Object> payload, String schema);
} 