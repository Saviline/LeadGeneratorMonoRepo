package submission.detail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import submission.core.domain.ValidationResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonSchemaValidatorTest {

    private JsonSchemaValidator validator;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        validator = new JsonSchemaValidator(objectMapper);
    }

    @Test
    void validate_ValidPayload_ShouldReturnSuccess() {
        String schema = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                        "email": {"type": "string", "format": "email"},
                        "name": {"type": "string"}
                    },
                    "required": ["email"]
                }
                """;

        Map<String, Object> payload = Map.of(
                "email", "test@example.com",
                "name", "Test User"
        );

        ValidationResult result = validator.validate(payload, schema);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void validate_MissingRequiredField_ShouldReturnFailure() {
        String schema = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                        "email": {"type": "string"},
                        "name": {"type": "string"}
                    },
                    "required": ["email", "name"]
                }
                """;

        Map<String, Object> payload = Map.of(
                "email", "test@example.com"
        );

        ValidationResult result = validator.validate(payload, schema);

        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void validate_InvalidType_ShouldReturnFailure() {
        String schema = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                        "age": {"type": "integer"}
                    },
                    "required": ["age"]
                }
                """;

        Map<String, Object> payload = Map.of(
                "age", "not-a-number"
        );

        ValidationResult result = validator.validate(payload, schema);

        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void validate_InvalidSchema_ShouldReturnFailureWithError() {
        String invalidSchema = "this is not valid json";

        Map<String, Object> payload = Map.of("email", "test@example.com");

        ValidationResult result = validator.validate(payload, invalidSchema);

        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Validation error"));
    }

    @Test
    void validate_EmptyPayload_AgainstRequiredFields_ShouldReturnFailure() {
        String schema = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                        "email": {"type": "string"}
                    },
                    "required": ["email"]
                }
                """;

        Map<String, Object> payload = Map.of();

        ValidationResult result = validator.validate(payload, schema);

        assertFalse(result.isValid());
    }

    @Test
    void validate_AdditionalProperties_ShouldPassByDefault() {
        String schema = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                        "email": {"type": "string"}
                    }
                }
                """;

        Map<String, Object> payload = Map.of(
                "email", "test@example.com",
                "extra", "This is extra data"
        );

        ValidationResult result = validator.validate(payload, schema);

        assertTrue(result.isValid());
    }

    @Test
    void validate_NumberRange_ShouldValidate() {
        String schema = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "type": "object",
                    "properties": {
                        "age": {"type": "integer", "minimum": 0, "maximum": 120}
                    },
                    "required": ["age"]
                }
                """;

        Map<String, Object> validPayload = Map.of("age", 25);
        Map<String, Object> invalidPayload = Map.of("age", 150);

        ValidationResult validResult = validator.validate(validPayload, schema);
        ValidationResult invalidResult = validator.validate(invalidPayload, schema);

        assertTrue(validResult.isValid());
        assertFalse(invalidResult.isValid());
    }
}
