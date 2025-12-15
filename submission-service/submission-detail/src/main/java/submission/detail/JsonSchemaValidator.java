package submission.detail;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import submission.core.domain.ValidationResult;
import submission.core.ports.IValidate;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JsonSchemaValidator implements IValidate {

    private final ObjectMapper objectMapper;

    @Override
    public ValidationResult validate(Map<String, Object> payload, String schema) {
        log.debug("Validating payload against schema");

        try {
            // Parse schema
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema jsonSchema = factory.getSchema(schema);

            // Convert payload to JsonNode
            JsonNode payloadNode = objectMapper.valueToTree(payload);

            // Validate
            Set<ValidationMessage> validationErrors = jsonSchema.validate(payloadNode);

            if (validationErrors.isEmpty()) {
                log.debug("Validation passed");
                return ValidationResult.success();
            }

            // Collect error messages
            List<String> errors = validationErrors.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.toList());

            log.debug("Validation failed with {} errors", errors.size());
            return ValidationResult.failure(errors);

        } catch (Exception e) {
            log.error("Validation error: {}", e.getMessage(), e);
            return ValidationResult.failure("Validation error: " + e.getMessage());
        }
    }
}